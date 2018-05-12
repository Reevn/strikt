package kirk

import kirk.api.expect
import kirk.assertions.all
import kirk.assertions.hasSize
import kirk.assertions.isUpperCase
import kirk.assertions.startsWith
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.*
import org.junit.jupiter.api.Assertions.assertEquals

object Reporting : Spek({

  describe("assertion failure messages") {
    on("evaluating a chained assertion that fails") {

      val e = fails {
        val subject = setOf("catflap", "rubberplant", "marzipan")
        expect(subject)
          .hasSize(3)
          .all { isUpperCase() }
          .all { startsWith('c') }
      }

      it("reports assertion statistics") {
        assertEquals(3, e.assertionCount, "Assertions")
        assertEquals(0, e.passCount, "Passed")
        assertEquals(3, e.failureCount, "Failed")
      }

      it("formats the error message") {
        val expectedLines = listOf(
          "▼ [catflap, rubberplant, marzipan]",
          "  ✔ has size 3",
          "  ✘ all elements match:",
          "    ▼ catflap",
          "      ✘ is upper case",
          "    ▼ rubberplant",
          "      ✘ is upper case",
          "    ▼ marzipan",
          "      ✘ is upper case",
          ""
        )
        val actualLines = e.message.lines()
        assertEquals(
          expectedLines.size,
          actualLines.size,
          "Expected ${expectedLines.size} lines of output but found ${actualLines.size}"
        )
        actualLines.forEachIndexed { i, line ->
          assertEquals(
            expectedLines[i],
            line,
            "Assertion failure message line ${i + 1}"
          )
        }
      }
    }

    on("evaluating a block assertion with multiple failures") {

      val e = fails {
        val subject = setOf("catflap", "rubberplant", "marzipan")
        expect(subject) {
          hasSize(0)
          all {
            isUpperCase()
            startsWith('c')
          }
        }
      }

      it("reports assertion statistics") {
        assertEquals(7, e.assertionCount, "Assertions")
        assertEquals(1, e.passCount, "Passed")
        assertEquals(6, e.failureCount, "Failed")
      }

      it("formats the error message") {
        val expectedLines = listOf(
          "▼ [catflap, rubberplant, marzipan]",
          "  ✘ has size 0",
          "    ↳ found 3",
          "  ✘ all elements match:",
          "    ▼ catflap",
          "      ✘ is upper case",
          "      ✔ starts with 'c'",
          "    ▼ rubberplant",
          "      ✘ is upper case",
          "      ✘ starts with 'c'",
          "    ▼ marzipan",
          "      ✘ is upper case",
          "      ✘ starts with 'c'",
          ""
        )
        val actualLines = e.message.lines()
        assertEquals(
          expectedLines.size,
          actualLines.size,
          "Expected ${expectedLines.size} lines of output but found ${actualLines.size}"
        )
        actualLines.forEachIndexed { i, line ->
          assertEquals(
            expectedLines[i],
            line,
            "Assertion failure message line ${i + 1}"
          )
        }
      }
    }
  }

})