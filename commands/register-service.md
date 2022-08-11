# register-service

Registers an additional service for reconciliation that can be used in the Ontotext Refine web interface.

The command registers the new service address by executing an request to Ontotext Refine REST API.
The result of the command is a message with the status of the request execution.

## Arguments

```bash
Usage:

ontorefine-cli register-service [-hV] -u <url> SERVICE

Description:
Registers an additional reconciliation service.

Parameters:
      SERVICE       The URL of the additional service that should be registered.

Options:
  -u, --url <url>   The URL of the Ontotext Refine instance to connect to, e.g.
                      http://localhost:7333.
  -h, --help        Show this help message and exit.
  -V, --version     Print version information and exit.
```

## Examples

TBD
