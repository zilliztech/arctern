/*
 * Copyright (C) 2019-2020 Zilliz. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.spark.sql.arctern.expressions

import org.apache.spark.sql.arctern.GeometryUDT
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.arctern.CodeGenUtil
import org.apache.spark.sql.catalyst.expressions.Expression
import org.apache.spark.sql.types.{ArrayType, ByteType, DataType}
import org.apache.spark.sql.catalyst.expressions.codegen._
import org.apache.spark.sql.catalyst.expressions.codegen.Block._

case class ST_GeomFromText(inputExpr: Seq[Expression]) extends Expression {

  assert(inputExpr.length == 1)

  override def nullable: Boolean = inputExpr.head.nullable

  override def eval(input: InternalRow): Any = {}

  override protected def doGenCode(ctx: CodegenContext, ev: ExprCode): ExprCode = {
    val wktExpr = inputExpr.head
    val wktGen = inputExpr.head.genCode(ctx)

    val resultCode =
      s"""
         |${CodeGenUtil.mutableGeometryInitCode(ev.value + "_geo")}
         |${ev.value}_geo = ${GeometryUDT.getClass().getName().dropRight(1)}.FromWkt(${wktGen.value}.toString());
         |${ev.value} = ${GeometryUDT.getClass().getName().dropRight(1)}.GeomSerialize(${ev.value}_geo);
       """.stripMargin

    if (nullable) {
      val nullSafeEval =
        wktGen.code + ctx.nullSafeExec(wktExpr.nullable, wktGen.isNull) {
          s"""
             |${ev.isNull} = false; // resultCode could change nullability.
             |$resultCode
             |""".stripMargin
        }
      ev.copy(code =
        code"""
          boolean ${ev.isNull} = true;
          ${CodeGenerator.javaType(ArrayType(ByteType, containsNull = false))} ${ev.value} = ${CodeGenerator.defaultValue(dataType)};
          $nullSafeEval
            """)
    }
    else {
      ev.copy(code =
        code"""
          ${wktGen.code}
          ${CodeGenerator.javaType(ArrayType(ByteType, containsNull = false))} ${ev.value} = ${CodeGenerator.defaultValue(dataType)};
          $resultCode
          """, FalseLiteral)
    }
  }

  override def dataType: DataType = new GeometryUDT

  override def children: Seq[Expression] = inputExpr
}
