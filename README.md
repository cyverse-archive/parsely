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

## Ensuring that an ontology is parseable.

__POST__ /parse

Accepts a JSON body that looks like this:

    {
        "url" : "URI to the ontology."
    }

Returns a 200 status and a JSON body like the following on success:

    {
        "url" : "<source>"
    }

Returns a 500 status and a JSON body like the following on failure:

    {
        "error_code" : "ERR_PARSE_FAILED",
        "url" : "<source>"
    }

## Get all of the classes defined in the ontology.

__GET__ /classes?url=ontology-url

Returns a 200 status and a JSON body that looks like the following on success:

    {
        "classes" : [
            {
                "namespace" : "<namespace URI>",
                "localname" : "<localname>",
                "url" : "<class URI>"
            }
        ]
    }

Returns a 500 status and a JSON body that looks like the following on failure:

    {
        "error_code" : "ERR_PARSE_FAILED",
        "url" : "<ontology URI>"
    }

## Get the applicable properties for a class in an ontology.

__GET__ /properties?url=ontology-url&class=class-uri

Make sure to URL encode the ontology and class URIs before making this call.

The class must be a class in the ontology. This endpoint also takes into account a classes superclasses when determining which properties to use.

Returns a 200 status and a JSON body that looks like the following on success:

    {
        "properties" : {
            {
                "namespace" : "<namespace URI>",
                "localname" : "<localname URI>",
                "url" : "<property URI>"
            }
        }
    }

Returns a 500 status and a JSON body like this if the ontology is unparseable:

    {
        "error_code" : "ERR_PARSE_FAILED",
        "url" : "<source>"
    }

Returns a 500 status and a JSON body like this if the class doesn't exist in the ontology:

    {
        "error_code" : "ERR_NOT_A_CLASS",
        "url" : "<ontology URL>",
        "class" : "<class URI>"
    }

## Get the triples contained in a file.

__GET__ /triples?url=triples-uri&type=file-type

Accepted values for 'type' are (without quotes): "RDF/XML", "RDF/XML-ABBREV", "N-TRIPLE", "TURTLE", "TTL", or "N3".

Returns a 200 status and a JSON body that looks like the following on success:

    {
        "triples" : {
            {
                "subject" : "subject URI",
                "predicate" : "predicate URI",
                "object" : "object URI"
            }
        }
    }

Returns a 500 status and a JSON body like this if the file is unparseable:

    {
        "error_code" : "ERR_PARSE_FAILED",
        "url" : "<source>"
    }
