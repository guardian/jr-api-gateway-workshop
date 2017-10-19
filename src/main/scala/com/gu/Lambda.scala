package com.gu.microserviceWorkshop

import com.typesafe.scalalogging.LazyLogging

object Lambda extends LazyLogging {

  def handler(): String = {
    "hello world"
  }

}