package models.db
package schema

import org.squeryl.{Schema, KeyedEntity}
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.PrimitiveTypeMode.inTransaction
import org.squeryl.annotations.Column
import java.util.Date
import scala.reflect._

// for Json lib
//import com.fasterxml.jackson.databind.JsonNode
//import com.fasterxml.jackson.annotation.{JsonIgnoreProperties, JsonIgnore}

import AppSchema._
import common._
// fields can be mutable or immutable - val or var



