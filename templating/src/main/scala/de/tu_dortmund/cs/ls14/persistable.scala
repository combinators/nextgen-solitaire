package de.tu_dortmund.cs.ls14

import _root_.java.nio.file.Path
import _root_.java.nio.file.Files
import _root_.java.nio.file.FileAlreadyExistsException

import scala.collection.JavaConverters._

trait Persistable {
  type T
  def rawText(elem: T): String
  def path(elem: T): Path

  /**
    * Computes the full path where to place `elem` relative to `basePath`.
    */
  def fullPath(basePath: Path, elem: T): Path = {
    basePath.resolve(path(elem))
  }


  /**
    * Persists this object to an object dependent path under `basePath`.
    * Overwrites any pre-existing files under `basePath` / `path`.
    */
  def persistOverwriting(basePath: Path, elem: T): Unit = {
    val fp = fullPath(basePath, elem)
    if (!Files.exists(fp.getParent))
      Files.createDirectories(fp.getParent)
    Files.write(fp, rawText(elem).getBytes)
  }

  /**
    * Persists this object to an object dependent path under `basePath`.
    * Throws an `FileAlreadyExistsException` if the file already exists.
    */
  def persist(basePath: Path, elem: T): Unit = {
    val fp = fullPath(basePath, elem)
    if (Files.exists(fp)) throw new FileAlreadyExistsException(fp.toString)
    else persistOverwriting(basePath, elem)
  }
}

object Persistable {
  type Aux[TT] = Persistable { type T = TT }

  def apply[T](implicit persistable: Aux[T]): Aux[T] = persistable
}