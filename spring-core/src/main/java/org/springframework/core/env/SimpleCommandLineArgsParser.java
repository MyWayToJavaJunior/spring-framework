/*
 * Copyright 2002-2011 the original author or authors.
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

package org.springframework.core.env;

import org.springframework.util.StringUtils;

/**
 * Parses a {@code String[]} of command line arguments in order to populate a
 * {@link CommandLineArgs} object.
 *
 * <h3>Working with option arguments</h3>
 * Option arguments must adhere to the exact syntax:
 * <pre class="code">--optName[=optValue]</pre>
 * That is, options must be prefixed with "{@code --}", and may or may not specify a value.
 * If a value is specified, the name and value must be separated <em>without spaces</em>
 * by an equals sign ("=").
 *
 * <h4>Valid examples of option arguments</h4>
 * <pre class="code">
 * --foo
 * --foo=bar
 * --foo="bar then baz"
 * --foo=bar,baz,biz</pre>
 *
 * <h4>Invalid examples of option arguments</h4>
 * <pre class="code">
 * -foo
 * --foo bar
 * --foo = bar
 * --foo=bar --foo=baz --foo=biz</pre>
 *
 * <h3>Working with non-option arguments</h3>
 * Any and all arguments specified at the command line without the "{@code --}" option
 * prefix will be considered as "non-option arguments" and made available through the
 * {@link CommandLineArgs#getNonOptionArgs()} method.
 *
 * @author Chris Beams
 * @since 3.1
 */
public class SimpleCommandLineArgsParser {

	public static String OPTION_NAME_PREFIX = "--";
	public static String OPTION_VALUE_SEPARATOR = "=";

	/**
	 * Parse the given {@code String} array based on the rules described {@linkplain
	 * SimpleCommandLineArgsParser above}, returning a fully-populated
	 * {@link CommandLineArgs} object.
	 * @param args command line arguments, typically from a {@code main()} method
	 */
	public CommandLineArgs parse(String... args) {
		CommandLineArgs commandLineArgs = new CommandLineArgs();
		for (String arg : args) {
			if (arg.startsWith(OPTION_NAME_PREFIX)) {
				String optionName = parseOptionName(arg);
				String optionValue = parseOptionValue(arg);
				if (StringUtils.isEmpty(optionName) || StringUtils.isEmpty(optionValue)) {
					throw new IllegalArgumentException("Invalid argument syntax: " + arg);
				}
				commandLineArgs.addOptionArg(optionName, optionValue);
			} else {
				commandLineArgs.addNonOptionArg(arg);
			}
		}
		return commandLineArgs;
	}

	public String parseOptionName(String arg) {
		String optionText = arg.substring(OPTION_NAME_PREFIX.length(), arg.length());
		if (!optionText.contains(OPTION_VALUE_SEPARATOR)) {
			return optionText;
		}
		return optionText.substring(0, optionText.indexOf(OPTION_VALUE_SEPARATOR));
	}

	public String parseOptionValue(String arg) {
		if (!arg.contains(OPTION_VALUE_SEPARATOR)) {
			return null;
		}
		String optionText = arg.substring(OPTION_NAME_PREFIX.length(), arg.length());
		return optionText.substring(optionText.indexOf(OPTION_VALUE_SEPARATOR) + 1, optionText.length());
	}

}
