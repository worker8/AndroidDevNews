package com.worker8.androiddevnews.util

/** copy from StackOverflow: https://stackoverflow.com/a/10187511/75579
 * Trims trailing whitespace. Removes any of these characters:
 * 0009, HORIZONTAL TABULATION
 * 000A, LINE FEED
 * 000B, VERTICAL TABULATION
 * 000C, FORM FEED
 * 000D, CARRIAGE RETURN
 * 001C, FILE SEPARATOR
 * 001D, GROUP SEPARATOR
 * 001E, RECORD SEPARATOR
 * 001F, UNIT SEPARATOR
 * @return "" if source is null, otherwise string with all trailing whitespace removed
 */
fun trimTrailingWhitespace(source: CharSequence): CharSequence {
    if (source.isBlank()) return source
    var i = source.length

    // loop back to the first non-whitespace character
    while (--i >= 0 && Character.isWhitespace(source[i])) {
    }
    return source.subSequence(0, i + 1)
}