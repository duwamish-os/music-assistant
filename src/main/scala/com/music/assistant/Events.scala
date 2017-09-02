package com.music.assistant

final case class AssistMeEvent(query: String, userId: String)
final case class AssistMeResponseNotification(query: String, response: String, userId: String)
final case class JobFailed(reason: String, job: AssistMeEvent)
case object SlaveRegistration
