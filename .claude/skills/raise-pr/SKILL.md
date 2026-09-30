---
name: raise-pr
description: Push the current ticket branch and open a GitHub pull request, with the body filled from .github/pull_request_template.md using the ticket, the diff, and the verification results. Use when the user says "raise a PR", "open a PR", "create pull request", or "ship this".
argument-hint: "[issue-number] [--draft]"
---

# Raise a PR

Arguments: `$ARGUMENTS`

## 1. Preconditions

- The current branch must not be `main`. If it is, stop and suggest creating a branch.
- `git status` must be clean. If it isn't, show the changes and ask whether to commit them.
- Work out the issue number from the argument, or from the branch name `<type>/<n>-<slug>`. If you can't, ask.
- If a PR already exists for this branch (`gh pr view --json url`), stop. Report its URL and offer to update its body instead.

## 2. Sync with main

```bash
git fetch origin
git rebase origin/main   # only if the branch has not been pushed yet or the user agrees
```

If there are conflicts, stop and report them. Don't resolve them silently. Don't force-push a branch someone else may have pulled unless the user agrees; use `--force-with-lease` when they do.

## 3. Verify

Run the `verify` skill, unless it passed on this exact commit earlier in the session. If checks fail, report them and ask whether to open the PR anyway. If the user says yes, open it as a draft.

## 4. Build the body

Fill every section of `.github/pull_request_template.md`:

- **Summary:** what the PR does and why, `Closes #<n>`, and the requirement IDs from the ticket.
- **Changes:** grouped from `git diff --stat origin/main...HEAD` and `git log origin/main..HEAD`. List migration files by name.
- **Acceptance criteria:** the ticket's checklist, ticked only where the code and a test prove it. Explain anything left unticked.
- **How to test:** concrete steps, including example requests or UI clicks.
- **Test evidence:** the actual result lines from `verify`. Never invent them. Write "not run" when a check wasn't run.
- **Checklist:** tick only the items you checked.

Delete template comments you filled in. Write the body to a scratchpad file.

## 5. Create

Title: a Conventional Commit style summary, e.g. `feat(recipe): recipe CRUD (#12)`.

```bash
git push -u origin HEAD
gh pr create --base main --title "<title>" --body-file <file> [--draft]
```

Add labels matching the ticket's labels (`gh issue view <n> --json labels`).

## 6. Report

Give the PR URL, whether it's a draft, and any unticked criteria or failing checks. Offer to run `review-ticket` on it.
