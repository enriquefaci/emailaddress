package uk.gov.hmrc.emailaddress


case class EmailAddress(value: String) {

  val (mailbox, domain): (Mailbox, Domain) = value match {
    case EmailAddress.validEmail(m, d) => (Mailbox(m), Domain(d))
    case invalidEmail => throw new IllegalArgumentException(s"'$invalidEmail' is not a valid email address")
  }

  override def toString: String = value

  lazy val obfuscated = ObfuscatedEmailAddress.apply(value)

  case class Mailbox private[EmailAddress] (value: String)
  case class Domain private[EmailAddress] (value: String)
}


object EmailAddress {
  final private[emailaddress] val validEmail = """\b([a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+)@([a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*)\b""".r

  def isValid(email: String) = email match {
    case validEmail(_,_) => true
    case invalidEmail => false
  }

  implicit def emailToString(e: EmailAddress): String = e.value
}

object PlayJsonFormats {
  import play.api.libs.json._

  implicit val emailAddressReads = new Reads[EmailAddress] {
    def reads(js: JsValue): JsResult[EmailAddress] = js.validate[String].flatMap {
      case s if EmailAddress.isValid(s) => JsSuccess(EmailAddress(s))
      case s => JsError("not a valid email address")
    }
  }
  implicit val emailAddressWrites = new Writes[EmailAddress] {
    def writes(e: EmailAddress): JsValue = JsString(e.value)
  }
}
