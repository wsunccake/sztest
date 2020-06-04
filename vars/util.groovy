def checkResponseStatus(String outputDir, String returnCode = '201') {
  def cmd1 = ["bash", "-c", "grep 'Response code:' ${outputDir}/*.out | wc -l"]
  def proc1 = Runtime.getRuntime().exec((String[]) cmd1.toArray())
  def totalResponse = proc1.text.trim() as Integer
  
  def cmd2 = ["bash", "-c", "grep 'Response code: ${returnCode}' ${outputDir}/*.out | wc -l"]
  def proc2 = Runtime.getRuntime().exec((String[]) cmd2.toArray())
  def successfulResponse = proc2.text.trim() as Integer

  println "total: ${totalResponse}"
  println "successful: ${successfulResponse}"
  
  def result = 'FAILURE'
  if (successfulResponse == 0) {
    result = 'FAILURE'
  }
  else if (successfulResponse == totalResponse) {
    result = 'SUCCESS'
  } else {
    result = 'UNSTABLE'
  }
  
  return result
}
