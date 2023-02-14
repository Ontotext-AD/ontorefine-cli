# Transform

Transforms a dataset into another specific format.

The command represents a composition of several other commands in order to allow complete transformation pipeline for
processing of datasets.
The phases of the command are:

- create project
- apply operations, if there are any
- export the data in the specified format using the provided mapping or SPARQL query
- delete the project

At the moment the command supports only transformation of CSV to RDF, but it will be gradually extended with more
options.

## Arguments

```bash
Usage:

ontorefine-cli transform [-hV] [--[no-]clean] [-c <configurations>] [-f
                         <format>] [-q <sparql>] [-r <result>] -u <url> [-a
                         <aliases>[,<aliases>...]]... FILE

Description:
Transforms given dataset into different data format.

Parameters:
      FILE                The file containing the data that should be
                            transformed. It should be a full name with one of
                            the supported extensions: (csv, tsv, json, xls,
                            xlsx, etc.).

Options:
  -u, --url <url>         The URL of the Ontotext Refine instance to connect
                            to, e.g. http://localhost:7333.
  -f, --format <format>   The format of the provided file. The default format
                            is 'csv'. Except 'csv', all other formats are in
                            experimental state. The allowed values are: csv,
                            tsv, excel, json, xml
  -c, --configurations <configurations>
                          A file with the configurations that should be used
                            for project creation. Ideally it should contain the
                            import options and the operations history of the
                            project, but it is also allowed for one one of the
                            configurations to be present. The mapping for the
                            RDFization of the dataset is stored as operation to
                            the history. The file should contain JSON document.
  -q, --sparql <sparql>   A file containing SPARQL CONSTRUCT query to be used
                            for RDFization of the provided dataset.
  -r, --result <result>   Controls the output format of the result. The default
                            format is 'turtle'. The allowed values are: rdfxml,
                            ntriples, turtle, turtlestar, trix, trig, trigstar,
                            binary, nquads, jsonld, rdfjson
  -a, --aliases <aliases>[,<aliases>...]
                          Aliases for the project. The argument accepts
                            multiple comma separated values.
      --[no-]clean        Controls the cleaning of the project after the
                            operation execution. When enabled the clean up will
                            be executed regardless of the success of the
                            transformation. By default the cleaning is enabled.
  -h, --help              Show this help message and exit.
  -V, --version           Print version information and exit.
```

## Examples

TBD
