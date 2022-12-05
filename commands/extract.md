# Extract

Extracts all operations applied to a project.

The command makes a request to Ontotext Refine to extract the history of the operations which were applied to the data.
The result from the command is a JSON document containing the applied operations or empty array, if no operations are
applied to the specified project.

## Arguments

```bash
Usage:

ontorefine-cli extract [-hV] [-m <mode>] -u <url> PROJECT

Description:
Extracts specific project configuration in JSON format.

Parameters:
      PROJECT         The project whose configurations to extract.

Options:
  -u, --url <url>     The URL of the Ontotext Refine instance to connect to, e.
                        g. http://localhost:7333.
  -m, --mode <mode>   Controls which project configuration to be extracted. The
                        default is 'operations'. The allowed values are:
                        operations, import-options, full.
  -h, --help          Show this help message and exit.
  -V, --version       Print version information and exit.
```

## Examples

TBD
