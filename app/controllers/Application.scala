package controllers

import play.api._
import play.api.mvc._
import play.api.libs.EventSource
import play.api.libs.json.Json
import play.api.libs.iteratee.Enumerator
import play.api.libs.iteratee.Concurrent
import scala.io.Source

object Application extends Controller {
  val (broadcast, _) = Concurrent.broadcast({ 
    Enumerator.enumerate( 
      Source.stdin.getLines.map( {i => 
        Json.toJson(i) 
      }) 
    )( play.api.libs.concurrent.Execution.Implicits.defaultContext )
  })
  
  def index = Action({
    Ok(views.html.index())
  })

  def pipe = Action({
    Ok.feed(
      broadcast &> EventSource()
    ).as("text/event-stream")
  })
}
