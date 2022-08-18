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

ontorefine-cli transform [-hV] [--[no-]clean] [-f <format>] [-o <operations>]
                         [-q <sparql>] [-r <result>] -u <url> FILE

Description:
Transforms given dataset into different data format.

Parameters:
      FILE                The file containing the data that should be
                            transformed. It should be a full name with one of
                            the supported extensions: (csv).

Options:
  -u, --url <url>         The URL of the Ontotext Refine instance to connect
                            to, e.g. http://localhost:7333.
  -f, --format <format>   The format of the provided file. The default format
                            is 'csv'. The allowed values are: csv
  -o, --operations <operations>
                          A file with the operations that should be applied to
                            the project. The mapping for the RDFization of the
                            dataset can be provided as operation. The file
                            should contain JSON document.
  -q, --sparql <sparql>   A file containing SPARQL CONSTRUCT query to be used
                            for RDFization of the provided dataset.
  -r, --result <result>   Controls the output format of the result. The default
                            format is 'turtle'. The allowed values are: rdfxml,
                            ntriples, turtle, turtlestar, trix, trig, trigstar,
                            binary, nquads, jsonld, rdfjson
      --[no-]clean        Controls the cleaning of the project after the
                            operation execution. When enabled the clean up will
                            be executed regardless of the success of the
                            transformation. By default the cleaning is enabled.
  -h, --help              Show this help message and exit.
  -V, --version           Print version information and exit.
```

## Examples

TBD
