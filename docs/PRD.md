# Recipe Manager — Product Requirements Document (PRD)

| | |
|---|---|
| **Status** | Draft v0.1 |
| **Owner** | Adithya S Sekhar |
| **Last updated** | 2026-09-30 |
| **Related** | [Technical Specification](./TECH_SPEC.md) |

> Items marked *(default)* are assumptions made during drafting. Confirm or change them before implementation.

---

## 1. Overview

Recipe Manager is a web application where people store their recipes, organize them with ingredients and tags, add photos, and find recipes with rich filters. Each user owns their recipes and can keep them private or publish them. Other users can then favorite, rate, comment on, and fork public recipes.

### 1.1 Problem

Home cooks keep recipes in notes apps, bookmarks, screenshots and paper cards. These are hard to search ("what can I make with chicken and no dairy in under 30 minutes?"), hard to share, and hard to adapt without losing the original.

### 1.2 Goals

1. Let a user capture a complete, structured recipe (ingredients with quantities, ordered steps, timings, nutrition, photos) in a few minutes.
2. Let any user find recipes quickly with text search combined with structured filters.
3. Let users share recipes publicly and adapt others' recipes by forking them.
4. Provide basic moderation so an admin can keep public content clean.
5. Run the whole stack locally with a single `docker compose up`.

### 1.3 Non-goals (v1)

- Native mobile apps (the web UI must still be responsive).
- Cloud or production deployment, HTTPS, or a CDN.
- Meal planning, shopping lists, and pantry tracking.
- Automatic unit conversion or recipe scaling math.
- Social graph features (following users, feeds, notifications).
- Importing recipes from URLs or third-party sites.
- OAuth / social login.

---

## 2. Personas

| Persona | Description | Primary needs |
|---|---|---|
| **Visitor** | Not logged in. | Browse and search public recipes, view recipe details. |
| **Home Cook** (User) | Registered user. | Create and manage their own recipes, keep some private, publish others, save favorites, rate, comment, fork. |
| **Admin** | Trusted operator. | Remove inappropriate recipes or comments, curate tags, disable abusive accounts. |

---

## 3. Features, user stories and acceptance criteria

IDs (e.g. `AUTH-1`) are referenced by the Tech Spec and by future tickets.

### 3.1 Authentication & accounts

**AUTH-1 Register.** *As a visitor, I want to create an account with my email and a password so that I can save recipes.*
- Email must be a valid format and unique (case-insensitive).
- Password must be at least 8 characters and contain a letter and a digit.
- A display name of 2–50 characters is required.
- On success the user is logged in automatically.
- A duplicate email shows "An account with this email already exists."

**AUTH-2 Log in / log out.** *As a user, I want to log in and out.*
- Valid credentials return a session (JWT) and redirect the user to "My Recipes".
- Invalid credentials show a generic "Invalid email or password" message, never saying which field was wrong.
- A disabled account cannot log in and sees "This account has been disabled."
- Log out clears the session on the client.
- The session expires after 24 hours *(default)*, after which the user must log in again.

**AUTH-3 Profile.** *As a user, I want to update my display name and password.*
- Changing the password requires the current password.

### 3.2 Recipe management

**REC-1 Create recipe.** *As a user, I want to create a recipe with structured details.*
- Required: title (3–120 chars), at least 1 ingredient, and at least 1 step.
- Optional: description (≤ 2000 chars), prep time (minutes, 0–1440), cook time (minutes, 0–1440), servings (1–100), difficulty (Easy / Medium / Hard), cuisine (from a fixed list, see Appendix A), nutrition per serving (calories, protein g, carbs g, fat g; all ≥ 0), tags, and images.
- Total time is derived as prep time + cook time.
- New recipes are **private** by default.
- On save, the user is taken to the recipe detail page.

**REC-2 Edit recipe.** *As the owner, I want to edit any field of my recipe.*
- Only the owner can edit.
- Steps can be added, removed and reordered.
- Ingredients can be added, removed and reordered.
- The "last updated" timestamp is shown on the detail page.

**REC-3 Delete recipe.** *As the owner, I want to delete my recipe.*
- A confirmation dialog is required.
- Deleting a recipe removes its images, ratings, comments and favorites.
- Forks of the deleted recipe remain, and their "forked from" link shows "original recipe deleted".

**REC-4 View recipe.** *As anyone allowed to see a recipe, I want a clear detail page.*
- The page shows the cover image, gallery, title, author, timings, servings, difficulty, cuisine, tags, ingredients (quantity, unit, name, note), numbered steps, nutrition, average rating and count, and comments.
- A private recipe viewed by anyone other than its owner or an admin returns "Not found" (it does not reveal that the recipe exists).

**REC-5 My Recipes.** *As a user, I want to see all my recipes, public and private.*
- The same filters and sort options as browsing (see §3.6) are available, plus a visibility filter.

### 3.3 Ingredients

**ING-1 Shared ingredient catalog.** *As a user, I want to pick ingredients from a shared list so that filtering by ingredient works across all recipes.*
- The ingredient field autocompletes from the catalog after 2 typed characters.
- If no match exists, the user can create a new ingredient inline.
- Ingredient names are unique case-insensitively and are stored trimmed and lower-cased for matching, with the original casing kept for display.

**ING-2 Quantity and unit.** *As a user, I want to specify how much of each ingredient.*
- Each recipe ingredient has an optional quantity (decimal > 0, e.g. 0.5), a unit from a fixed list (Appendix B) *(default)*, and an optional note (≤ 100 chars, e.g. "finely chopped").
- The same ingredient cannot appear twice in one recipe.

### 3.4 Tags

**TAG-1 Tag recipes.** *As a user, I want to add tags like "vegan" or "quick" to my recipes.*
- Tags are global and shared by all users.
- The tag input autocompletes existing tags. Any user can create a new tag.
- Tag names are 2–30 chars, stored lower-case, and may contain letters, digits, spaces and hyphens.
- A recipe can have at most 10 tags.

**TAG-2 Browse by tag.** Clicking a tag on a recipe opens the browse page filtered by that tag.

### 3.5 Images

**IMG-1 Cover image.** *As a user, I want to upload a cover photo for my recipe.*
- One cover image per recipe.
- If there is no cover, a placeholder image is shown.

**IMG-2 Gallery.** *As a user, I want to add extra photos.*
- Up to 5 gallery images per recipe *(default)*.
- Gallery images can be reordered and deleted, and any gallery image can be promoted to cover.

**IMG-3 Upload rules.**
- Accepted formats are JPG, PNG and WebP, with a maximum of 5 MB per file *(default)*.
- Invalid files are rejected with a clear message naming the reason (type or size).
- An upload progress indicator is shown.
- The server generates a thumbnail (≈ 400 px wide) for list views *(default)*.

### 3.6 Search, filters, sorting and pagination

**SRCH-1 Browse public recipes.** Visitors and users can browse all public recipes.

**SRCH-2 Text search.** A keyword search matches the title, description and ingredient names. Results rank by relevance when a search term is present.

**SRCH-3 Filters** (all combinable, AND between different filters):

| Filter | Behaviour |
|---|---|
| Tags | Multi-select. Recipe must have **all** selected tags. |
| Cuisine | Multi-select. Recipe matches **any** selected cuisine. |
| Difficulty | Multi-select. Matches **any** selected difficulty. |
| Include ingredients | Recipe must contain **all** listed ingredients. |
| Exclude ingredients | Recipe must contain **none** of the listed ingredients. |
| Max total time | prep + cook ≤ N minutes. Recipes with no time set are excluded when this filter is active. |
| Calories range | min ≤ calories per serving ≤ max. Recipes with no calories set are excluded when this filter is active. |

**SRCH-4 Sorting.** Newest (default), Highest rated, and Quickest (total time ascending). Relevance is added as an option, and becomes the default, when a search term is present.

**SRCH-5 Pagination.** Results are paginated server-side, 12 per page *(default)*. The page shows the total result count and page controls.

**SRCH-6 Shareable filter state.** The active search, filters, sort and page are reflected in the URL so a filtered view can be bookmarked or shared.

### 3.7 Social features

**SOC-1 Visibility toggle.** *As an owner, I want to make a recipe public or private.*
- The toggle is available on the editor and on the detail page.
- Making a recipe private hides it from browse and search immediately. Existing ratings, comments and favorites are kept but are not visible to others.

**SOC-2 Favorites.** *As a user, I want to bookmark recipes.*
- A user can favorite any public recipe, including their own.
- A "Favorites" page lists them with the same filters and sorting.
- If a favorited recipe becomes private, it disappears from the non-owner's Favorites list until it is made public again.

**SOC-3 Ratings.** *As a user, I want to rate a public recipe from 1 to 5 stars.*
- Each user has one rating per recipe and can change or remove it.
- Owners cannot rate their own recipes.
- The recipe shows its average (1 decimal place) and rating count.

**SOC-4 Comments.** *As a user, I want to comment on public recipes.*
- Comments are 1–1000 characters of plain text, shown newest first.
- An author can edit or delete their own comment. Edited comments show "(edited)".
- The recipe owner can delete any comment on their recipe.
- Comments are flat, with no threaded replies in v1.

**SOC-5 Fork.** *As a user, I want to copy a public recipe into my own collection and modify it.*
- A fork creates a new **private** recipe owned by the forking user. It copies all fields, ingredients, steps, tags and images, and is titled "<original title> (fork)".
- The fork shows "Forked from <original title> by <author>", linking to the original while that recipe is public.
- Ratings, comments and favorites are not copied.
- The original shows its fork count.

### 3.8 Administration

**ADM-1 Moderate content.** An admin can view any recipe, including private ones, and can delete any recipe or comment. Deletions are logged with the admin, the target and a timestamp.

**ADM-2 Manage tags.** An admin can rename, merge (reassign recipes from tag A to tag B, then delete A) and delete tags.

**ADM-3 Manage ingredients.** An admin can rename and merge duplicate catalog ingredients.

**ADM-4 Manage users.** An admin can list and search users, and disable or re-enable accounts. A disabled user cannot log in, and their public recipes are hidden while the account is disabled.

**ADM-5 Bootstrap admin.** The first admin account is created from environment variables at startup (see Tech Spec).

---

## 4. Permissions matrix

| Action | Visitor | User (non-owner) | Owner | Admin |
|---|:-:|:-:|:-:|:-:|
| View public recipe | ✅ | ✅ | ✅ | ✅ |
| View private recipe | ❌ | ❌ | ✅ | ✅ |
| Create recipe | ❌ | ✅ | — | ✅ |
| Edit recipe / manage its images | ❌ | ❌ | ✅ | ❌ *(default: admins moderate by deleting, not editing)* |
| Delete recipe | ❌ | ❌ | ✅ | ✅ |
| Toggle visibility | ❌ | ❌ | ✅ | ❌ |
| Favorite | ❌ | ✅ (public) | ✅ | ✅ |
| Rate | ❌ | ✅ (public) | ❌ | ✅ (public) |
| Comment | ❌ | ✅ (public) | ✅ | ✅ |
| Delete a comment | ❌ | own only | own + any on own recipe | any |
| Fork | ❌ | ✅ (public) | ✅ | ✅ (public) |
| Create tag / ingredient | ❌ | ✅ | ✅ | ✅ |
| Rename / merge / delete tag or ingredient | ❌ | ❌ | ❌ | ✅ |
| Disable users | ❌ | ❌ | ❌ | ✅ |

---

## 5. Screens and user flows

| # | Screen | Route *(indicative)* | Access |
|---|---|---|---|
| 1 | Browse / Search | `/` | All |
| 2 | Recipe detail | `/recipes/:id` | Per visibility |
| 3 | Recipe editor (create / edit) | `/recipes/new`, `/recipes/:id/edit` | Owner |
| 4 | My Recipes | `/me/recipes` | User |
| 5 | Favorites | `/me/favorites` | User |
| 6 | Profile / settings | `/me/profile` | User |
| 7 | Login / Register | `/login`, `/register` | Visitor |
| 8 | Admin: users, tags, ingredients, audit log | `/admin/*` | Admin |

**Key flows**

1. **Capture a recipe:** Register → My Recipes → New → fill details, ingredients and steps → upload cover → Save (private) → toggle Public.
2. **Find a recipe:** Browse → type "chicken" → filter tag "quick", exclude "peanut", max time 30 → sort Highest rated → open a result → Favorite.
3. **Adapt a recipe:** Open a public recipe → Fork → the editor opens on the private copy → modify → Save.
4. **Moderate:** Admin opens a reported recipe → Delete → the action is recorded in the audit log.

**Layout notes**
- Browse page: a filter panel (a drawer on mobile), a card grid (cover thumbnail, title, total time, difficulty, rating), and pagination.
- Editor: sectioned form (Basics, Ingredients, Steps, Nutrition, Tags, Images) with inline validation. Unsaved-changes warning on navigation.

---

## 6. Non-functional requirements

| Area | Requirement |
|---|---|
| **Responsive UI** | Fully usable from 360 px to desktop widths. Filters collapse into a drawer on small screens. |
| **Accessibility** | Keyboard-navigable forms, labelled inputs, alt text on images (defaults to the recipe title), and WCAG AA colour contrast. |
| **Performance** | Browse/search API p95 < 500 ms with 10k recipes on a local machine *(default)*. Thumbnails are used in lists. |
| **Security** | BCrypt password hashing, JWT auth, server-side authorization on every endpoint, validation of every input, upload type checked by content (not only by extension), no stack traces in API errors. |
| **Validation** | The same rules are enforced on the client (for UX) and on the server (authoritative). The server returns field-level errors. |
| **Reliability** | Data and images persist across container restarts through Docker volumes. |
| **Observability** | Structured logs and a Spring Boot Actuator health endpoint. |
| **API docs** | OpenAPI 3 spec served through Swagger UI. |
| **Quality** | Automated backend and frontend tests run in CI on every push and pull request. The build fails on test failure. |
| **Portability** | The whole stack starts with `docker compose up` with no host dependencies other than Docker. |

---

## 7. Release scope

### v1 (this document)
Everything in §3.

### Later ideas (not committed)
- Shopping list generated from recipes
- Servings scaling with unit conversion
- Import a recipe from a URL
- Follow users and an activity feed
- Collections / cookbooks
- Content reporting by users
- OAuth login
- Cloud deployment with S3 image storage

---

## 8. Open questions

1. Should admins be able to **edit** other users' recipes, or only delete them? (Current default: delete only.)
2. Is a gallery limit of 5 images and a 5 MB file limit acceptable?
3. Should the cuisine list be fixed (Appendix A) or user-extensible like tags?
4. Should users be able to report recipes or comments to admins in v1?
5. Is email verification or password reset needed in v1? (Currently out of scope because there is no mail server in local Docker.)

---

## Appendix A — Cuisines *(default)*

American, Chinese, French, Greek, Indian, Italian, Japanese, Korean, Mediterranean, Mexican, Middle Eastern, Spanish, Thai, Vietnamese, Other.

## Appendix B — Units *(default)*

| Type | Units |
|---|---|
| Weight | g, kg, oz, lb |
| Volume | ml, l, tsp, tbsp, cup, fl oz |
| Count | piece, clove, slice, pinch, can, bunch |
| None | (no unit, e.g. "2 eggs"), to taste |
