package condition.util

import kotlin.text.Regex

/**
 *표준 정규식 패턴 모음
 */
object Patterns {
    /** 이메일 주소 ( RFC 5322 ) */
    val EMAIL = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$")

    /** HTTP/HTTPS URL */
    val URL = Regex("^(https?://)?([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?\$")

    /** UUID (RFC 4122) */
    val UUID = Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89ABab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}\$")

    /** 전화번호 (국제/국내) */
    val PHONE = Regex("^\\+?[0-9]{1,3}[- ]?[0-9]{1,4}[- ]?[0-9]{4,10}\$")

    /**날짜 (YYYY-MM-DD) */
    val DATE = Regex("^\\d{4}-\\d{2}-\\d{2}\$")
}
