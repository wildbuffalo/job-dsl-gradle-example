import groovy.json.JsonSlurper
def jsonSlurper = new JsonSlurper()
def object = jsonSlurper.parseText('''{"report": {
    "totalScenarios": 5,
    "totalFailed": 4,
    "totalSuccess": 1,
    "totalSkipped": 0,
    "totalUndefined": 0}}''')
println(object)
assert object.report.totalUndefined == 0
