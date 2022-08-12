# Extract

Extracts all of the operations that are applied on project data.

The command makes a request to Ontotext Refine to extract the history of the operations that are applied to the data.
The result from the command should be an JSON document containing the applied operations or empty array, if no
operations are applied to the specified project yet.

## Arguments

```bash
Usage:

ontorefine-cli extract [-hV] -u <url> PROJECT

Description:
Extracts the operations of a project in JSON format.

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
