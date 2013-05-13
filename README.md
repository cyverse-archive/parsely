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

## Get the list of supported file types

__GET__ /type-list

Returns a 200 status and a JSON body that looks like the following on success:

    {
        "types" : ["csv, "tsv"]
    }

No error codes should be returned with this endpoint.

## Get the file types associated with a file.

__GET__ /type?user=username&path=/path/to/irods/file

Returns a 200 status and a JSON body that looks like the following on success:

    {
        "types" : ["csv"]
    }

Possible returned types are:

* tsv
* csv

The values are determined by looking at the values associated with the ipc-filetype attribute in the AVUs
associated with the file.

Possible error codes:

* ERR_DOES_NOT_EXIST
* ERR_NOT_A_USER
* ERR_NOT_READABLE

## Add a file type to a file.

__POST__/type?user=username

The POSTed body should look like the following:

    {
        "path" : "/path/to/irods/file",
        "type" : "csv"
    }

Accepted values for the "type" field are:

* tsv
* csv

Returns a 200 status and a JSON body that looks like the following on success:

    {
        "path" : "/path/to/irods/file",
        "type" : "csv"
    }

Returns a 500 status and a JSON body like this if something goes wrong:

    {
        "error_code" : "ERR_PARSE_FAILED",
        "url" : "<source>"
    }

Possible error codes are:

* ERR_NOT_OWNER
* ERR_BAD_OR_MISSING_FIELD
* ERR_DOES_NOT_EXIST
* ERR_NOT_A_USER

## Delete a file type from a file.

__DELETE__ /type?user=username&path=/irods/path&type=csv

Accepted values for the "type" field are:

* tsv
* csv

Returns a 200 status and a JSON body that looks like the following on success:

    {
        "path" : "/path/to/irods/file",
        "type" : "csv"
    }

Returns a 500 status and a JSON body like this if something goes wrong:

    {
        "error_code" : "ERR_NOT_OWNER",
        "url" : "<source>"
    }

Possible error codes are:

* ERR_NOT_OWNER
* ERR_BAD_OR_MISSING_FIELD
* ERR_DOES_NOT_EXIST
* ERR_NOT_A_USER

## Look up paths in a user's home directory based on file type

__GET__ /type/paths?user=username&type=type-string

Accepted values for the "type" parameter:

* tsv
* csv

URL encode as appropriate.

Returns a 200 status and a JSON body that looks like the following on success:

    {
        "paths" : ["/path/to/irods/file"]
    }

Returns a 500 status and a JSON body like this if something goes wrong:

    {
        "error_code" : "ERR_PARSE_FAILED",
        "url" : "<source>"
    }

Possible error codes are:

* ERR_NOT_A_USER

