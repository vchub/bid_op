package domain


trait Phrase {
  def id: Long
  def network_phrase_id: String
  def phrase: String
}

