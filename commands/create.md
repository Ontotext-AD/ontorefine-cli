# Create

Creates a Refine project using the provided dataset.

The command uploads the dataset to Ontotext Refine, which creates an project with a unique identifier.
The project identifier is returned as a response from the command.

## Arguments

```bash
Usage:

ontorefine-cli create [-hV] [-c <configurations>] [-f <format>] [-n <name>] -u
                      <url> [-a <aliases>[,<aliases>...]]... FILE

Description:
Creates a new project from a file.

Parameters:
      FILE                The file that will be used to create the project. It
                            should be a full name with one of the supported
                            extensions (csv, tsv, json, txt, xls, xlsx, js,
                            etc.).

Options:
  -u, --url <url>         The URL of the Ontotext Refine instance to connect
                            to, e.g. http://localhost:7333.
  -n, --name <name>       A name for the Refine project. If not provided, the
                            file name will be used.
  -f, --format <format>   The format of the provided file. The default format
                            is 'csv'. Except 'csv', all other formats are in
                            experimental state. The allowed values are: csv,
                            tsv, excel, json, xml
  -c, --configurations <configurations>
                          File containing configurations for the importing
                            process of the dataset. It includes information how
                            to parse the input data so that it can be
                            represented in tabular form and additioanl options
                            related to the project creation.
  -a, --aliases <aliases>[,<aliases>...]
                          Aliases for the project. The argument accepts
                            multiple comma separated values.
  -h, --help              Show this help message and exit.
  -V, --version           Print version information and exit.
```

## Examples

TBD
