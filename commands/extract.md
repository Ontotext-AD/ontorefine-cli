# Extract

Extracts all operations applied to a project.

The command makes a request to Ontotext Refine to extract the history of the operations which were applied to the data.
The result from the command is a JSON document containing the applied operations or empty array, if no operations are
applied to the specified project.

## Arguments

```bash
Usage:

ontorefine-cli extract [-hV] -u <url> PROJECT

Description:
Extracts the operations history of a project in JSON format.

Parameters:
      PROJECT       The project whose operations to extract.

Options:
  -u, --url <url>   The URL of the Ontotext Refine instance to connect to, e.g.
                      http://localhost:7333.
  -h, --help        Show this help message and exit.
  -V, --version     Print version information and exit.
```

## Examples

TBD
