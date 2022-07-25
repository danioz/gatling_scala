package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import baseConfig.BaseConfig

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

class MetaDSLSimulation extends BaseConfig {

  val gettingCars = scenario("Get all cars and specific car")
    .exec(goToCars)
    .pause(1)
    .exec(getSpecificCar(2))
    .exec(getSpecificCar(3))
    .pause(1, 5)
    .exec(getSpecificCarFromCSV())
    .pause(1, 3)
    .exec(goToCars)

  setUp(
    gettingCars.inject(
      incrementUsersPerSec(5.0)
        .times(5)
        .eachLevelLasting(1)
        .separatedByRampsLasting(2)
        .startingFrom(2) // Double
    )
  ).protocols(httpProtocol)
}
