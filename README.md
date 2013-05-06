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

## Get the triples contained in a file.

__GET__ /triples?url=triples-uri&type=file-type&user=user

Accepted values for 'type':

* RDF/XML
* RDF/XML-ABBREV
* N-TRIPLE
* TURTLE
* TTL
* N3
* TSV
* CSV

Returns a 200 status and a JSON body that looks like the following on success:

    {
        "triples" : {
            {
                "subject" : "subject",
                "predicate" : "predicate",
                "object" : "object"
            }
        }
    }

Returns a 500 status and a JSON body like this if the file is unparseable:

    {
        "error_code" : "ERR_PARSE_FAILED",
        "url" : "<source>"
    }

If the protocol for the 'url' value is irods:// or is omitted, then the file will be
retrieved from iRODS. If the protocol is http://, the file will be downloaded and parsed.

If the file being parsed is a CSV or a TSV file, the the first column will be the subject,
the second column will be the predicate, and the third column will be the object. Any
columns past the first three will be ignored. If the CSV/TSV contains too few columns, then
the needed columns will be set to an empty string.