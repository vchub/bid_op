package models.db
package schema

trait SchemaFactory {
  /**
  * runs block
  * used for Dependency Injection (DI)
  * @param block of code
  * @return T
  */
  def making_DI_possible [T]()(block: ⇒ T): T = {
    block
  }
}

class AppSchemaFactory {
  def get_TimeSlotType():TimeSlotType = {new TimeSlotType}
}

object AppSchemaFactory{
}
