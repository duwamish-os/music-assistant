package com.music.assistant

final case class AssistMeJob(text: String)
final case class AssistMeResult(text: String)
final case class JobFailed(reason: String, job: AssistMeJob)
case object SlaveRegistration
