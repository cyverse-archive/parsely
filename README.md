# parsely

A simple web app - written in Clojure - that can parse ontologies, list the classes defined within, and list the properties applicable to  a class.

# Configuration

Create a file called parsely.properties. Add the following line to it.

    parsely.app.listen-port = 31326

Now, export the IPLANT_CONF_DIR environment variable, pointing it to the directory containing the parsely.properties file.

# Running

This assumes that you have leiningen installed.

0. Configure parsely as described above.

1. Run 'lein deps'.

2. Run 'lein ring server-headless'

# Endpoints

__NOTE__: The user parameters don't do anything at the moment. Put whatever you want there. It's likely to be removed completely in the very near future.

## Ensuring that an ontology is parseable.

__POST__ /parse?user=<user>

Accepts a JSON body that looks like this:

    {
        "source" : "URI to the ontology."
    }

Returns a 200 status and a JSON body like the following on success:

    {
        "user" : "<user>",
        "source" : "<source>"
    }

Returns a 500 status and a JSON body like the following on failure:

    {
        "error_code" : "ERR_PARSE_FAILED",
        "ontology" : "<source>"
    }

## Get all of the classes defined in the ontology.

__GET__ /classes?user=<user>&ontology=<URI>

Returns a 200 status and a JSON body that looks like the following on success:

    {
        "classes" : [
            {
                "namespace" : "<namespace URI>",
                "localname" : "<localname>",
                "uri" : "<class URI>"
            }
        ]
    }

Returns a 500 status and a JSON body that looks like the following on failure:

    {
        "error_code" : "ERR_PARSE_FAILED",
        "ontology" : "<ontology URI>"
    }

## Get the applicable properties for a class in an ontology.

__GET__ /properties?user=<user>&ontology=<URI>&class=<class URI>

Make sure to URL encode the ontology and class URIs before making this call.

The class must be a class in the ontology. This endpoint also takes into account a classes superclasses when determining which properties to use.

Returns a 200 status and a JSON body that looks like the following on success:

    {
        "properties" : {
            {
                "namespace" : "<namespace URI>",
                "localname" : "<localname URI>",
                "uri" : "<property URI>"
            }
        }
    }

Returns a 500 status and a JSON body like this if the ontology is unparseable:

    {
        "error_code" : "ERR_PARSE_FAILED",
        "ontology" : "<source>"
    }

Returns a 500 status and a JSON body like this if the class doesn't exist in the ontology:

    {
        "error_code" : "ERR_NOT_A_CLASS",
        "ontology" : "<ontology URI>",
        "class" : "<class URI>"
    }
