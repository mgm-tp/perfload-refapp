#!/bin/sh
#
# Copyright (c) 2013 mgm technology partners GmbH
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#


cd `dirname $0`
[ -f setenv.sh ] && . ./setenv.sh

if [ "x$JAVA_HOME" = "x" ] ; then
	echo ERROR: JAVA_HOME not found in your environment.
	echo Please set the JAVA_HOME variable in your environment to match the
	echo location of your Java installation
	exit -1
fi

if [ ! -f "$JAVA_HOME/bin/java" ] ; then
	echo ERROR: JAVA_HOME is set to an invalid directory.
	echo JAVA_HOME = "$JAVA_HOME"
	echo Please set the JAVA_HOME variable in your environment to match the
	echo location of your Java installation
	exit -1
fi

JAVA_CMD="$JAVA_HOME/bin/java"

[ -f logback.xml ] && JAVA_OPTS="$JAVA_OPTS -Dlogback.configurationFile=logback.xml"

JAVA_OPTS="$JAVA_OPTS -Xmx256m -jar ./lib/perfload-ref-app-${project.version}.jar $@"

exec "$JAVA_CMD" $JAVA_OPTS