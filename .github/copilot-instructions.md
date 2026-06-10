# Repo Agent Routing

- For Jira-key-driven QA requests such as `KAN-2` or `Fetch Jira- KAN-2`, delegate to the `parent-agent` skill instead of doing a standalone Jira lookup.
- Treat `Fetch Jira- <KEY>` as the entrypoint for the full QA orchestration workflow defined by `parent-agent`.
- Only do a direct Jira fetch when the user explicitly asks for issue details only, summary only, or metadata only.