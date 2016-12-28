/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api

import org.gradle.integtests.fixtures.AbstractIntegrationSpec

class InitScriptExecutionIntegrationSpec extends AbstractIntegrationSpec {
    def "notices changes to settings scripts that do not change the file length"() {
        def initScript = file("init.gradle")
        initScript.text = "println 'counter: __'"

        expect:
        (10..25).each {
            int before = buildFile.length()
            initScript.text = "println 'counter: $it'"
            assert buildFile.length() == before

            executer.withArguments("--init-script", initScript.absolutePath)
            succeeds()
            result.assertOutputContains("counter: $it")
        }
    }

}
