package baseConfig

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

class BaseConfig extends Simulation {

  val httpProtocol = http
    .baseUrl("http://localhost:3000")
    .check(status.is(200))
//    .proxy(Proxy("localhost",8888).httpsPort(8888))


  private val carsEndpointPath = "/cars"

  val carId = null

  protected val goToCars: HttpRequestBuilder = http("got to all cars").get(carsEndpointPath)
  protected val saveToCars: HttpRequestBuilder = http("save to cars").post(carsEndpointPath)

  val csvFeeder = csv("./src/test/resources/data/carCsvFile.csv").circular

  before {
    println("Simulation is about to start!")
  }

  after {
    println("Simulation is finished!")
  }

  protected def getAllCars() = {
    repeat(1) {
      exec(goToCars
        .check(status.is(200)))
    }
  }

  protected def getSpecificCar(id: Int) = {
    http("Go to car: " + id)
      .get(s"${carsEndpointPath}/$id")
      .check(status.is(200))
  }

  protected def getSpecificCarFromCSV() = {
    repeat(10) {
      feed(csvFeeder)
        .exec(http("Go to specific car from CSV")
          .get("/cars/${id}")
          .check(jsonPath("$.model").is("${model}"))
          .check(jsonPath("$.brand").is("${brand}"))
          .check(status.is(200)))
        .pause(1)
    }
  }


  protected def addCar() = {
    repeat(1) {
      exec(saveToCars
        .body(RawFileBody("./src/test/resources/bodies/carRequestBody.json")).asJson
        .check(jsonPath("$.id").saveAs("carId"))
        .check(status.in(200 to 201)))
    }
  }

  protected def updateCar(id: Int) = {
    http("Update a car: " + id)
      .put(s"${carsEndpointPath}/$id")
      .body(RawFileBody("./src/test/resources/bodies/carRequestBody.json")).asJson
      .check(status.in(200, 201))
  }

  protected def deleteCar(id: Int) = {
    http("Delete a car: " + id)
      .delete(s"${carsEndpointPath}/$id")
      .check(status.is(200))
  }
}
