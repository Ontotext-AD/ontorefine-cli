# create

Creates a refine project using given dataset.

The command uploads the provided dataset to Ontotext Refine, which creates an project with unique identifier.
The project identifier is returned as response from the command.

## Arguments

```bash
Usage:

ontorefine-cli create [-hV] [-f <format>] [-n <name>] -u <url> FILE

Description:
Creates a new project from a file.

Parameters:
      FILE                The file that will be used to create the project. It
                            should be a full name with one of the supported
                            extensions (csv).

Options:
  -u, --url <url>         The URL of the Ontotext Refine instance to connect
                            to, e.g. http://localhost:7333.
  -n, --name <name>       The name of the OntoRefine project to create. If not
                            provided, the file name will be used.
  -f, --format <format>   The format of the provided file. The default format
                            is 'csv'. The allowed values are: csv
  -h, --help              Show this help message and exit.
  -V, --version           Print version information and exit.
```

## Examples

TBD