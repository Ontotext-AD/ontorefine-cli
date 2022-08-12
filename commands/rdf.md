# RDF

Exports the data of a given project in RDF format.

The command extracts and converts the data of specified project into RDF format using the internal SPARQL repository
of the Ontotext Refine.
The result of the command is the project data in specific RDF format.

## Arguments

```bash
Usage:

ontorefine-cli rdf [-hV] [-f <format>] [-m <mapping>] [-q <sparql>] -u <url>
                   PROJECT

Description:
Exports the data of a project to RDF format.

Parameters:
      PROJECT               The project whose data to convert.

Options:
  -u, --url <url>           The URL of the Ontotext Refine instance to connect
                              to, e.g. http://localhost:7333.
  -q, --sparql <sparql>     A file containing SPARQL CONSTRUCT query to be used
                              for RDFization of the provided dataset.
  -m, --mapping <mapping>   The mapping that will be used for the RDF
                              conversion. The file should contain JSON
                              configuration. If not provided the process will
                              try to retrieve it from the project
                              configurations, if it is defined there.
  -f, --format <format>     Controls the format of the result. The default
                              format is 'turtle'. The allowed values are:
                              rdfxml, ntriples, turtle, turtlestar, n3, trix,
                              trig, trigstar, binary, nquads, jsonld, rdfjson
  -h, --help                Show this help message and exit.
  -V, --version             Print version information and exit.
```

## Examples

TBD
