# Update-aliases

Updates the aliases of a specified project.

The command can add and remove aliases in a single invocation.
The result of the command is a message with the status of the execution.

## Arguments

```bash
Usage:

ontorefine-cli update-aliases [-hV] -u <url> [-a <add>[,<add>...]]... [-r
                              <remove>[,<remove>...]]... PROJECT

Description:
Updates project aliases. The command can add and remove values in a single
invocation.

Parameters:
      PROJECT       The identifier of the project to which the transformation
                      operations will be applied.

Options:
  -u, --url <url>   The URL of the Ontotext Refine instance to connect to, e.g.
                      http://localhost:7333.
  -a, --add <add>[,<add>...]
                    Aliases to add to the project. The argument accepts
                      multiple comma separated values.
  -r, --remove <remove>[,<remove>...]
                    Aliases to remove from the project. The argument accepts
                      multiple comma separated values.
  -h, --help        Show this help message and exit.
  -V, --version     Print version information and exit.
```

## Examples

TBD
