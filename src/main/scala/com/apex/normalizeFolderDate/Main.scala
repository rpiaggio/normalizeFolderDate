package com.apex.normalizeFolderDate

import java.nio.file.FileAlreadyExistsException
import java.time.Month

import better.files._
import better.files.Dsl._

object Main {

  private val monthMap = Month.values.map(m => m.toString.take(3) -> f"${m.getValue}%02d").toMap

  private val IsoNoDash = """(\d{4})(\d{2})(\d{2})""".r
  private val Reversed = """(\d{1,2})-(\d{1,2})-(\d{4})""".r
  private val ReversedWithMonthName = """(\d{1,2})-(\w{3})-(\d{2})""".r

  def main(args: Array[String]): Unit = {
    val dir = args.headOption.map(_.toFile).getOrElse(cwd)

    println(s"Working in [$dir]")

    ls(dir).filter(_.isDirectory).foreach { file =>
      val name = file.name
      val newName =
        name match {
          case IsoNoDash(year, month, date) => s"$year-$month-$date"
          case Reversed(date, month, year) => f"$year-${month.toInt}%02d-${date.toInt}%02d"
          case ReversedWithMonthName(date, monthName, year) => f"20${year.toInt}%02d-${monthMap(monthName.toUpperCase)}-${date.toInt}%02d"
          case _ => name
        }

      if (newName != name) {
        print(s"[$name] => [$newName]: ")
        try {
          file.renameTo(newName)
          println("OK!")
        } catch {
          case _: FileAlreadyExistsException => println("ALREADY EXISTS!")
        }
      }
    }
  }
}
