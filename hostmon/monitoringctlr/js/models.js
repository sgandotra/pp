/**
 * Created with JetBrains WebStorm.
 * User: sagandotra
 * Date: 4/7/15
 * Time: 12:58 PM
 * To change this template use File | Settings | File Templates.
 */

exports.models = {
      "_Nmon" : {
          "id" : "_Nmon",
          "required" : ["frequency","samples","graphite"],
          "properties" : {
              "frequency" : {
                  "type" : "integer",
                  "format" : "int32",
                  "description" : "Frequency for publishing nmon data",
                  "minimum" : "10",
                  "maximum" : "100"

              },
              "samples" : {
                  "type" : "integer",
                  "format" : "int64",
                  "description" : "The number of samples published by nmon. Duration = (frequency * samples)",
                  "minimum" : "1000",
                  "maximum" : "1000000"
              },
              "graphite" : {
                  "type" : "boolean",
                  "description" : " should data be published to graphite"
              }
          }

      },
      "nmon" : {
          "id" : "nmon",
          "required" : ["_Nmon"] ,
          "properties" : {
              "nmon"   : {
                  "$ref" : "_Nmon",
                  "description" : "nmon payload"
              }
          }
      },
      "Jmon" : {
          "id" : "Jmon",
          "required" : ["services"],
          "properties" : {
              "services" : {
                  "type"  : "array",
                  "description" : "service names to activate",
                  "items" : {
                      "$ref" : "javaServiceName"
                  }
              }
          }
      },
      "javaServiceName" : {
          "id" : "javaServiceName",
          "required" : ["name"],
          "properties" : {
              "name" : {
                  "type" : "string",
                  "description" : " service name"
              }
          }
      },
      "NodeMon" : {
        "id" : "NodeMon",
        "required" : ["services"],
        "properties" : {
            "services" : {
                "type"  : "array",
                "description" : "service names to activate",
                "items" : {
                    "$ref" : "nodeMonServiceName"
                }
            }
        }
     },
    "nodeMonServiceName" : {
        "id" : "nodeMonServiceName",
        "required" : ["name"],
        "properties" : {
            "name" : {
                "type" : "string",
                "description" : " service name"
            }
        }
    },
    "Jmeter":{
        "id":"Jmeter",
        "required": ["script", "config"],
        "properties":{
            "script":{
                "type":"string",
                "description": "JMeter input request"
            },
            "config":{
                "$ref":"_config",
                "description": "Jmeter script configuration"
            }
        }
    },
    "_config" : {
        "id" : "_config",
        "required" : ['vusers','duration','stagename1'],
        "properties" : {
            "vusers" : {
                "type" : "string",
                "description"  : "vusers"
            },
            "duration" : {
                "type" : "string",
                "description" : "duration of test"
            },
            "stagename1" : {
                "type" : "string",
                "description" : "stage name"
            }
        }
    }





};