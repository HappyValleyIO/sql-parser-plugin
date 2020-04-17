package io.happyvalley.sqlparserplugin

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.quotes.Transform
import arrow.meta.quotes.dotQualifiedExpression
import arrow.meta.quotes.scope
import net.sf.jsqlparser.JSQLParserException
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression

data class SqlCompilationError(val sql: String, val e: Throwable) : RuntimeException("Could not compile SQL: $sql", e)

fun String.sql(): String = this

val Meta.sqlParser: CliPlugin
    get() =
        "Sql Parser" {
            meta(
                    dotQualifiedExpression({
                        selectorExpression is KtCallExpression
                                && receiverExpression is KtStringTemplateExpression
                                && selectorExpression.scope().toString() == "sql()"
                    }) { expression ->
                        val stringVal = (receiverExpression.value as KtStringTemplateExpression).text.trim('"')
                        try {
                            CCJSqlParserUtil.parse(stringVal)
                            Transform.replace(
                                    replacing = expression,
                                    newDeclaration = receiverExpression
                            )
                        } catch (e: JSQLParserException) {
                            throw SqlCompilationError(stringVal, e)
                        }
                    }
            )
        }