package com.gu.microserviceWorkshop
import java.io.{InputStream, OutputStream}

import com.typesafe.scalalogging.LazyLogging
import io.circe.parser._
import io.circe.syntax._
import java.nio.charset.StandardCharsets.UTF_8
import cats.syntax.either._



object Lambda extends LazyLogging {

  def handler(): String = {
    "hello world"
  }

}
