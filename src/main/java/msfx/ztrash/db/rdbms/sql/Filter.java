/*
 * Copyright (c) 2021-2024 Miquel Sas.
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
package msfx.ztrash.db.rdbms.sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import msfx.ztrash.db.Field;
import msfx.ztrash.db.Value;
import msfx.ztrash.db.rdbms.DBEngine;
import msfx.ztrash.db.rdbms.SQL;
import msfx.lib.util.Lists;
import msfx.lib.util.Strings;
import msfx.lib.util.iterators.ArrayIterator;
import msfx.lib.util.iterators.FunctionIterator;

/**
 * Builder of generic filter clauses.
 *
 * @author Miquel Sas
 */
@SuppressWarnings("ConstantConditions")
public abstract class Filter extends SQL {

	/**
	 * Condition types.
	 */
	private enum Type { COMPARE, EXISTS, IN, LIKE, IS_NULL }

	/**
	 * Supported comparison operators.
	 */
	private static final String[] COMPARISON = new String[] { "=", ">", ">=", "<", "<=", "!=" };
	/**
	 * Supported existence operators.
	 */
	private static final String[] EXISTS = new String[] { "EXISTS", "NOT EXISTS" };
	/**
	 * Supported inclusion operators.
	 */
	private static final String[] INCLUSION = new String[] { "IN", "NOT IN" };
	/**
	 * Supported like operators.
	 */
	private static final String[] LIKE = new String[] { "LIKE", "NOT LIKE" };
	/**
	 * Supported logical operators.
	 */
	private static final String[] LOGICAL = new String[] { "AND", "AND NOT", "OR", "OR NOT" };
	/**
	 * Supported null operators.
	 */
	private static final String[] NULL = new String[] { "IS NULL", "IS NOT NULL" };

	/**
	 * A condition. Conditions are chained within a segment.
	 */
	private static class Condition {

		/**
		 * Condition type.
		 */
		private final Type type;
		/**
		 * Logical operator.
		 */
		private final String logOp;
		/**
		 * Left expression.
		 */
		private final String leftExpr;
		/**
		 * Comparison operator.
		 */
		private final String compOp;
		/**
		 * Right expression.
		 */
		private final String rightExpr;
		/**
		 * Select object in EXISTS conditions.
		 */
		private Select select;

		/**
		 * Copy constructor.
		 *
		 * @param c The condition to copy.
		 */
		private Condition(Condition c) {
			this.type = c.type;
			this.logOp = c.logOp;
			this.leftExpr = c.leftExpr;
			this.compOp = c.compOp;
			this.rightExpr = c.rightExpr;
			this.select = c.select;
		}
		/**
		 * Constructor.
		 *
		 * @param type      Condition type.
		 * @param logOp     Logical operator.
		 * @param leftExpr  Left expression.
		 * @param compOp    Comparison operator.
		 * @param rightExpr Right expression.
		 */
		private Condition(
				Type type,
				String logOp,
				String leftExpr,
				String compOp,
				String rightExpr) {
			this.type = type;
			this.logOp = logOp;
			this.leftExpr = leftExpr;
			this.compOp = compOp;
			this.rightExpr = rightExpr;
		}
	}

	/**
	 * A segment chains a list of conditions.
	 */
	private static class Segment {

		/**
		 * Logical operator to link segments.
		 */
		private String logOp;
		/**
		 * List of conditions.
		 */
		private final List<Condition> conditions = new ArrayList<>();

		/**
		 * Copy constructor.
		 *
		 * @param seg The segment to copy.
		 */
		private Segment(Segment seg) {
			this.logOp = seg.logOp;
			for (Condition c : seg.conditions) {
				conditions.add(new Condition(c));
			}
		}
		/**
		 * Constructor.
		 *
		 * @param logOp Logical operator.
		 */
		private Segment(String logOp) {
			this.logOp = logOp;
		}

		/**
		 * Add a condition to the list of conditions.
		 *
		 * @param type      Condition type.
		 * @param logOp     Logical operator to chain the condition.
		 * @param leftExpr  Left expression of the condition.
		 * @param compOp    Comparison operator.
		 * @param rightExpr Right expression of the condition.
		 */
		private void add(
				Type type,
				String logOp,
				String leftExpr,
				String compOp,
				String rightExpr) {
			conditions.add(new Condition(type, logOp, leftExpr, compOp, rightExpr));
		}
		/**
		 * Check whether the segment is empty.
		 *
		 * @return A boolean.
		 */
		private boolean isEmpty() {
			return conditions.isEmpty();
		}
	}

	/**
	 * List of segments.
	 */
	private final List<Segment> segments = new ArrayList<>();

	/**
	 * Constructor, adds the first empty segment without logical operator because it is not
	 * required.
	 *
	 * @param db The underlying database engine.
	 */
	public Filter(DBEngine db) {
		super(db);
		segments.add(new Segment((String) null));
	}

	/**
	 * Add the first segment condition of type <i>FIELD IS NULL</i>.
	 *
	 * @param field  The field.
	 * @param nullOp The is null operator.
	 */
	public void condition(Field field, String nullOp) {
		condition(null, field, nullOp);
	}
	/**
	 * Add the first segment condition of type <i>FIELD = VALUE</i> or <i>FIELD LIKE VALUE</i>.
	 *
	 * @param field The field.
	 * @param oper  Comparison or like operator.
	 * @param value The value to compare with.
	 */
	public void condition(Field field, String oper, Value value) {
		condition(null, field, oper, value);
	}
	/**
	 * Add the first segment condition of type <i>FIELD IN (VALUE1, ...)</i>.
	 *
	 * @param field  The field.
	 * @param inOp   Inclusion operator.
	 * @param values List of values.
	 */
	public void condition(Field field, String inOp, Value... values) {
		condition(null, field, inOp, values);
	}
	/**
	 * Add the first segment condition of type <i>FIELD IN (VALUE1, ...)</i>.
	 *
	 * @param field  The field.
	 * @param inOp   Inclusion operator.
	 * @param values Collection of values.
	 */
	public void condition(Field field, String inOp, Collection<Value> values) {
		condition(null, field, inOp, values);
	}

	/**
	 * Add the first segment EXISTS condition with en entire SELECT object.
	 *
	 * @param existsOp Exists operator.
	 * @param select   Select query.
	 */
	public void condition(String existsOp, Select select) {
		condition(null, existsOp, select);
	}

	/**
	 * Add a chained condition of type <i>FIELD IS NULL</i>.
	 *
	 * @param logOp  Logical operator.
	 * @param field  The field.
	 * @param nullOp The is null operator.
	 */
	public void condition(String logOp, Field field, String nullOp) {
		validate(true, logOp, LOGICAL);
		validate(false, nullOp, NULL);
		condition(logOp, field.getNameParent(), nullOp);
	}
	/**
	 * Add a chained condition of type <i>FIELD = VALUE</i> or <i>FIELD LIKE VALUE</i>.
	 *
	 * @param logOp Logical operator.
	 * @param field The field.
	 * @param oper  Comparison or like operator.
	 * @param value The value to compare with.
	 */
	public void condition(String logOp, Field field, String oper, Value value) {
		validate(true, logOp, LOGICAL);
		validate(false, oper, COMPARISON, LIKE);
		condition(logOp, field.getNameParent(), oper, db.toStringSQL(value));
	}
	/**
	 * Add a chained condition of type <i>FIELD IN (VALUE1, ...)</i>.
	 *
	 * @param logOp  Logical operator.
	 * @param field  The field.
	 * @param inOp   Inclusion operator.
	 * @param values List of values.
	 */
	public void condition(String logOp, Field field, String inOp, Value... values) {
		validate(true, logOp, LOGICAL);
		validate(false, inOp, INCLUSION);
		condition(logOp, field.getNameParent(), inOp, toStringList(values));
	}
	/**
	 * Add a chained condition of type <i>FIELD IN (VALUE1, ...)</i>.
	 *
	 * @param logOp  Logical operator.
	 * @param field  The field.
	 * @param inOp   Inclusion operator.
	 * @param values Collection of values.
	 */
	public void condition(String logOp, Field field, String inOp, Collection<Value> values) {
		validate(true, logOp, LOGICAL);
		validate(false, inOp, INCLUSION);
		condition(logOp, field.getNameParent(), inOp, toStringList(values.iterator()));
	}

	/**
	 * Add an EXISTS condition with en entire SELECT object.
	 *
	 * @param logOp    Logical operator.
	 * @param existsOp Exists operator.
	 * @param select   Select query.
	 */
	public void condition(String logOp, String existsOp, Select select) {
		validate(true, logOp, LOGICAL);
		validate(false, existsOp, EXISTS);
		if (select == null) throw new NullPointerException("Null select");
		condition(logOp, existsOp, select.toSQL());
		List<Condition> conditions = Lists.getLast(segments).conditions;
		Lists.getLast(conditions).select = select;
	}

	/**
	 * Add a condition to the last segment.
	 *
	 * @param args List of arguments.
	 */
	public void condition(String... args) {

		/*
		 * Must have at least 3 arguments. If the first argument is null, then the last segment
		 * must be empty. If the first argument is not null, but the last segment is empty, then
		 * the first argument is considered to be null.
		 */
		if (args == null) {
			throw new NullPointerException("List of arguments can not be null");
		}
		if (args.length < 3) {
			throw new IllegalArgumentException("Two arguments are at least required");
		}
		if (args.length > 4) {
			throw new IllegalArgumentException("Four arguments is the maximum admitted");
		}

		/*
		 * Build a list with arguments.
		 */
		LinkedList<String> queue = new LinkedList<>();
		for (String arg : args) {
			queue.addLast(arg);
		}

		/*
		 * Logical operator to chain the condition. If the last segment is not empty, then the
		 * logical operator can not be null.
		 */
		String logOp = queue.removeFirst();
		if (logOp == null && !Lists.getLast(segments).isEmpty()) {
			throw new IllegalArgumentException("First argument must be a logical operator");
		}
		if (Lists.getLast(segments).isEmpty()) {
			logOp = null;
		}

		/*
		 * Case "EXISTS" expression.
		 */
		if (Strings.in(queue.getFirst(), EXISTS)) {
			/* Number of arguments must be 3. */
			if (args.length != 3) {
				String error = "Invalid number of arguments for an EXISTS expression";
				throw new IllegalArgumentException(error);
			}
			String relOp = queue.removeFirst();
			String rightExpr = queue.removeFirst();
			Lists.getLast(segments).add(Type.EXISTS, logOp, null, relOp, rightExpr);
			return;
		}

		/*
		 * Left expression. Can not be a comparison operator.
		 */
		String leftExpr = queue.removeFirst();
		boolean invalidLeftExpr = false;
		invalidLeftExpr |= Strings.in(leftExpr, COMPARISON);
		invalidLeftExpr |= Strings.in(leftExpr, INCLUSION);
		invalidLeftExpr |= Strings.in(leftExpr, LIKE);
		invalidLeftExpr |= Strings.in(leftExpr, NULL);
		if (invalidLeftExpr) {
			String error = "Left expression can not be a comparison operator: " + leftExpr;
			throw new IllegalArgumentException(error);
		}

		/*
		 * Relational operator. Must be one of COMPARISON, INCLUSION, LIKE, or NULL.
		 */
		String relOp = queue.removeFirst();
		boolean validRelOp = false;
		validRelOp |= Strings.in(relOp, COMPARISON);
		validRelOp |= Strings.in(relOp, INCLUSION);
		validRelOp |= Strings.in(relOp, LIKE);
		validRelOp |= Strings.in(relOp, NULL);
		if (!validRelOp) {
			String error = "Relational operator is not valid: " + relOp;
			throw new IllegalArgumentException(error);
		}

		/*
		 * Case "IS NULL" expression. Check there are no more arguments.
		 */
		if (Strings.in(relOp, NULL)) {
			if (!queue.isEmpty()) {
				String error = "Too many arguments for a IS NULL condition";
				throw new IllegalArgumentException(error);
			}
			Lists.getLast(segments).add(Type.IS_NULL, logOp, leftExpr, relOp, null);
			return;
		}

		/*
		 * Case "IN" expression, right argument must be present.
		 */
		if (Strings.in(relOp, INCLUSION)) {
			if (queue.isEmpty()) {
				String error = "Missing right expression for a IN condition";
				throw new IllegalArgumentException(error);
			}
			String rightExpr = queue.removeFirst();
			Lists.getLast(segments).add(Type.IN, logOp, leftExpr, relOp, rightExpr);
			return;
		}

		/*
		 * Case "LIKE" expression, right argument must be present.
		 */
		if (Strings.in(relOp, LIKE)) {
			if (queue.isEmpty()) {
				String error = "Missing right expression for a LIKE condition";
				throw new IllegalArgumentException(error);
			}
			String rightExpr = queue.removeFirst();
			Lists.getLast(segments).add(Type.LIKE, logOp, leftExpr, relOp, rightExpr);
			return;
		}

		/* Case general comparison expression. */
		if (Strings.in(relOp, COMPARISON)) {
			if (queue.isEmpty()) {
				String error = "Missing right expression for a COMPARISON condition";
				throw new IllegalArgumentException(error);
			}
			String rightExpr = queue.removeFirst();
			Lists.getLast(segments).add(Type.COMPARE, logOp, leftExpr, relOp, rightExpr);
			return;
		}

		throw new IllegalArgumentException("Invalid arguments to complete the condition");
	}

	/**
	 * Check whether this where clause is empty.
	 *
	 * @return A boolean.
	 */
	public boolean isEmpty() { return segments.size() == 1 && segments.get(0).isEmpty(); }

	/**
	 * Add a new segment. If the last segment is empty nothing is added.
	 *
	 * @param logOp The logical operator to chain the segment.
	 */
	public void segment(String logOp) {
		if (Lists.getLast(segments).isEmpty()) {
			return;
		}
		if (!Strings.in(logOp, LOGICAL)) {
			throw new IllegalArgumentException("Invalid logical operator: " + logOp);
		}
		segments.add(new Segment(logOp));
	}

	/**
	 * Returns this statement as a formatted SQL string.
	 *
	 * @param formatted A boolean that indicates whether the result query should be formatted to be
	 *                  readable.
	 * @return The query.
	 */
	public String toSQL(boolean formatted) {
		StringBuilder sql = new StringBuilder();
		for (Segment segment : segments) {
			if (segment.logOp != null) {
				String logOp = " " + segment.logOp + " ";
				if (formatted) {
					sql.append("\n");
					sql.append(Strings.blank(PAD - logOp.length()));
				}
				sql.append(logOp);
			}
			sql.append("(");
			for (Condition condition : segment.conditions) {
				if (formatted) {
					sql.append("\n");
				}
				if (condition.logOp != null) {
					String logOp = " " + condition.logOp + " ";
					if (formatted){
						sql.append(Strings.blank(PAD - logOp.length()));
					}
					sql.append(logOp);
				} else {
					if (formatted) {
						sql.append(Strings.blank(PAD));
					}
				}
				sql.append(toSQL(condition, formatted));
			}
			if (formatted) {
				sql.append("\n");
				sql.append(Strings.blank(PAD));
			}
			sql.append(")");
		}
		return sql.toString();
	}
	/**
	 * Returns the condition as an SQL string.
	 *
	 * @param condition The condition.
	 * @param formatted A boolean that indicates whether the result query should be formatted to be
	 *                  readable.
	 * @return The query part.
	 */
	private String toSQL(Condition condition, boolean formatted) {
		StringBuilder sql = new StringBuilder();
		if (condition.type == Type.COMPARE) {
			sql.append(condition.leftExpr);
			sql.append(" ");
			sql.append(condition.compOp);
			sql.append(" ");
			sql.append(condition.rightExpr);
		}
		if (condition.type == Type.EXISTS) {
			sql.append(condition.compOp);
			sql.append(" ");
			if (formatted) {
				sql.append("(");
				sql.append("\n");
				sql.append(condition.select.toSQL(formatted));
				sql.append("\n");
				sql.append(Strings.blank(PAD));
				sql.append(")");
			} else {
				sql.append("(");
				sql.append(condition.rightExpr);
				sql.append(")");
			}
		}
		if (condition.type == Type.IN) {
			sql.append(condition.leftExpr);
			sql.append(" ");
			sql.append(condition.compOp);
			sql.append(" ");
			sql.append("(");
			sql.append(condition.rightExpr);
			sql.append(")");
		}
		if (condition.type == Type.LIKE) {
			sql.append(condition.leftExpr);
			sql.append(" ");
			sql.append(condition.compOp);
			sql.append(" ");
			sql.append(condition.rightExpr);
		}
		if (condition.type == Type.IS_NULL) {
			sql.append(condition.leftExpr);
			sql.append(" ");
			sql.append(condition.compOp);
		}
		return sql.toString();
	}

	/**
	 * Return a string representation of the WHERE part in a more readable format.
	 */
	@Override
	public String toString() { return toSQL(true); }

	/**
	 * Convert an array of values to the string list applying the DBEngine conversion.
	 *
	 * @param values The list of values.
	 * @return The string list.
	 */
	private String toStringList(Value... values) {
		return toStringList(new ArrayIterator<>(values));
	}
	/**
	 * Convert an array of values to the string list applying the DBEngine conversion.
	 *
	 * @param values The list of values.
	 * @return The string list.
	 */
	private String toStringList(Iterator<Value> values) {
		Function<Value, String> function = db::toStringSQL;
		return Lists.toString(new FunctionIterator<>(values, function));
	}

	/**
	 * Validate that the operator is within the list of operators.
	 *
	 * @param acceptNull A boolean that indicates whether the operator can bel null.
	 * @param op         The operator.
	 * @param opss       The list of lists of operators.
	 */
	private void validate(boolean acceptNull, String op, String[]... opss) {
		if (acceptNull && op == null) {
			return;
		}
		if (!Strings.in(op, opss)) {
			throw new IllegalArgumentException("Invalid operator \"" + op + "\" for condition");
		}
	}

	/**
	 * Add an entire filter.
	 *
	 * @param filter The filter to add.
	 */
	protected void filter(Filter filter) {
		filter(null, filter);
	}
	/**
	 * Add an entire filter.
	 *
	 * @param logOp  The logical operator to chain the first segment.
	 * @param filter The filter to add.
	 */
	protected void filter(String logOp, Filter filter) {
		if (Lists.getLast(segments).isEmpty()) {
			Lists.removeLast(segments);
			logOp = null;
		} else {
			if (logOp == null || !Strings.in(logOp, LOGICAL)) {
				throw new IllegalArgumentException("Invalid operator logical operator");
			}
		}
		for (int i = 0; i < filter.segments.size(); i++) {
			Segment segment = new Segment(filter.segments.get(i));
			if (i == 0) segment.logOp = logOp;
			segments.add(segment);
		}
	}
}
