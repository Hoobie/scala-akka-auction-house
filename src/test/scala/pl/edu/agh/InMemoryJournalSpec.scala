package pl.edu.agh

import akka.persistence.journal.JournalSpec
import com.typesafe.config.ConfigFactory

class InMemoryJournalSpec extends JournalSpec(config = ConfigFactory.parseString(
  """
akka.persistence.journal.plugin = "inmemory-journal"
akka.persistence.snapshot-store.plugin = "inmemory-snapshot-store"
  """))
