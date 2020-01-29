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

class BasicSimulation extends Simulation {

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
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  val scn = scenario("Scenario Name") // A scenario is a chain of requests and pauses
    .exec(http(endpointUrl)
      .get("/"))

  setUp(
      scn.inject(
        atOnceUsers(0),                                 // Start with 0 users
        rampUsers(MAX_NB_USERS) during (TEST_DURATION seconds)     // Constantly increase nb of users to MAX_NB_USERS during 1 minute
      ).protocols(httpProtocol))
}
