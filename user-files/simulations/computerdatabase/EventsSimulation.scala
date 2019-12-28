/*
 * Copyright 2011-2019 GatlingCorp (https://gatling.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package computerdatabase

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class EventsSimulation extends Simulation {

  val serverUrl = System.getProperty("SERVER_DOMAIN")
  val endpointUrl = System.getProperty("SERVER_ENDPOINT")
  val MAX_NB_USERS = System.getProperty("MAX_NB_USERS").toInt
  val TEST_DURATION = System.getProperty("TEST_DURATION").toInt

  val httpProtocol = http
    .baseUrl(serverUrl) // Here is the root for all relative URLs
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // Here are the common headers
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.34 (KHTML, like Gecko) Version/11.0 Mobile/15A5341f Safari/604.1")

  object randomStringGenerator {
    def randomString(length: Int) = scala.util.Random.alphanumeric.filter(_.isLetter).take(length).mkString
  }

  object ThrottleEvents {

      // Each user is sending events payload every 2 seconds during 60 seconds
      val throttle = repeat(30, "i") { // Note how we force the counter name so we can reuse it
         exec(http("Post ${i}")
           .post(endpointUrl+"?cusID=${RAND_SESS}")
             .body(StringBody("""{"events":[{"action":"SOUND_ON","short_code":"988np6"}],"times":{"988np6":4290},"sessionIds":{"988np6":"${RAND_SESS}_test"},"test":true}""")).asJson
             .check(status.is(200)))
         .pause(2)
       }
  }

  // Create random session id for EACH user
  val feeder = Iterator.continually(Map("RAND_SESS" -> ("841sdk0febrn_"+randomStringGenerator.randomString(10))))

  val scn = scenario("Scenario Name") // A scenario is a chain of requests and pauses
    .feed(feeder)
    .exec(ThrottleEvents.throttle)

   setUp(
    scn.inject(
      atOnceUsers(0),                                 // Start with 0 users
      rampUsers(MAX_NB_USERS) during (TEST_DURATION seconds)     // Constantly increase nb of users to MAX_NB_USERS during 1 minute
    ).protocols(httpProtocol))
}
