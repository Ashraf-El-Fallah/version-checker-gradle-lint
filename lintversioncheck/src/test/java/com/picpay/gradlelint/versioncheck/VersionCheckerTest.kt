package com.picpay.gradlelint.versioncheck

import com.android.tools.lint.client.api.LintDriver
import com.android.tools.lint.client.api.LintRequest
import com.android.tools.lint.detector.api.GradleContext
import com.picpay.gradlelint.versioncheck.api.FakeLintClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File

@RunWith(JUnit4::class)
internal class VersionCheckerTest {

    /***
     * Integration Test (using fake API responses)
     */
    @Suppress("UnstableApiUsage")
    @Test
    fun `when trigger with any dependency Should report message to IDE`() {
        val versionChecker = VersionChecker()
        val currentDir = File("")
        val client = FakeLintClient("response/maven_central_response.json")

        val driver = LintDriver(
            registry = GradleVersionCheckerRegistry(),
            client = client,
            request = LintRequest(client, listOf(currentDir))
        )

        val context = GradleContext(
            gradleVisitor = client.getGradleVisitor(),
            driver = driver,
            project = client.getProject(File("test/resources"), currentDir),
            main = null,
            file = currentDir
        )

        versionChecker.checkDslPropertyAssignment(
            context,
            "",
            "Libs.retrofit",
            "dependencies",
            parentParent = null,
            valueCookie = Any(),
            statementCookie = Any()
        )

        val expectedMessage = "New version available: com.squareup.retrofit2:retrofit:2.8.1\n" +
                "Actual: com.squareup.retrofit2:retrofit:2.6.4"

        assertTrue(client.messagesFromContext.isNotEmpty())
        assertEquals(expectedMessage, client.messagesFromContext.first())
    }
}
