package ai.ivira.app.utils.ui

enum class ApiErrorCodes(val value: String) {
    InvalidInputData("invalidInputData"),
    UrlNotFound("invalidToken"),
    OtpAlreadyExists("otpAlreadyExists")
}