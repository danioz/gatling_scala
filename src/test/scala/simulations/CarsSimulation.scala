package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import baseConfig.BaseConfig

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

class CarsSimulation extends BaseConfig {

  val gettingCars = scenario("Get all cars and specific car")
    .exec(goToCars)
    .pause(1)
    .exec(getSpecificCar(2))
    .exec(getSpecificCar(3))
    .pause(1, 5)
    .exec(getSpecificCarFromCSV())
    .pause(1, 3)
    .exec(goToCars)

  val addingCar = scenario("Add a new car")
    .exec(getAllCars())
    .pause(2)
    .exec(addCar())
    .pause(2, 5)
    .exec(getAllCars())

  setUp(
    gettingCars.inject(
      nothingFor(5),
      atOnceUsers(1),
      rampUsers(1) during (10)
    ),

    addingCar.inject(
      nothingFor(10),
      atOnceUsers(1),
      constantUsersPerSec(1) during (10 seconds),
      rampUsersPerSec(1) to (5) during (20 seconds)
    )
  ).protocols(httpProtocol)
}

