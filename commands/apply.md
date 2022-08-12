# Apply

Applies operations to a specified project data.

The command uses the provided JSON document with operations to apply them to the project data.
The result of the command should be message with the status of the execution.

## Arguments

```bash
Usage:

ontorefine-cli apply [-hV] -u <url> OPERATIONS PROJECT

Description:
Applies transformation operations to a project.

Parameters:
      OPERATIONS    The file with the operations that should be applied to the
                      project. The file should be a JSON file.
      PROJECT       The identifier of the project to which the transformation
                      operations will be applied.

Options:
  -u, --url <url>   The URL of the Ontotext Refine instance to connect to, e.g.
                      http://localhost:7333.
  -h, --help        Show this help message and exit.
  -V, --version     Print version information and exit.
```

## Examples

TBD
