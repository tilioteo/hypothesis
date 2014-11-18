/*
 * Copyright 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.vaadin.gwt.i18n.client;

import org.vaadin.gwt.i18n.client.impl.PatternInfo;

/**
 * A concrete implementation of NumberFormat that understands decimal numbers.
 */
public class DecimalFormat extends NumberFormat {

  protected DecimalFormatSymbols formatSymbols;
  protected String posPrefix;
  protected String posSuffix;
  protected String negPrefix;
  protected String negSuffix;
 
  // original versions of the above before currency symbol substitution
  /*protected String curPosPrefix;
  protected String curPosSuffix;
  protected String curNegPrefix;
  protected String curNegSuffix;*/
 
  protected int multiplier;
  protected int groupingSize;
  protected boolean useExponential;
  protected int minExponentDigits;
  protected boolean forceDecimalSeparator;
  //protected boolean monetary;
  //protected boolean useCurrencyCode;
  protected String pattern;
 
  // Used to keep track of prefix/suffix monetary flags separately;
  // since they are set separately, we wouldn't be able to clear
  // the monetary flag otherwise.
  @SuppressWarnings("unused")
  private boolean monetaryPrefix;
  @SuppressWarnings("unused")
  private boolean monetarySuffix;

  public DecimalFormat() {
    this(new DecimalFormatSymbols());
  }
 
  public DecimalFormat(String pattern) {
    this();
    applyPattern(pattern);
  }
 
  public DecimalFormat(String pattern, DecimalFormatSymbols formatSymbols) {
    this(formatSymbols);
    applyPattern(pattern);
  }

  protected DecimalFormat(DecimalFormatSymbols formatSymbols) {
    this.formatSymbols = formatSymbols;
    posPrefix = posSuffix = negSuffix = "";
    negPrefix = String.valueOf(formatSymbols.getMinusSign());
    multiplier = 1;
  }

  public void applyLocalizedPattern(String pattern) {
    pattern = pattern.replace(formatSymbols.getDecimalSeparator(), '.');
    pattern = pattern.replace(formatSymbols.getGroupingSeparator(), ',');
    pattern = pattern.replace(formatSymbols.getDigit(), '#');
    pattern = pattern.replace(formatSymbols.getExponentSeparator(), "E");
    pattern = pattern.replace(formatSymbols.getPercent(), '%');
    pattern = pattern.replace(formatSymbols.getPerMill(), '\u2030');
    pattern = pattern.replace(formatSymbols.getZeroDigit(), '0');
    pattern = pattern.replace(formatSymbols.getMinusSign(), '-');
    pattern = pattern.replace(formatSymbols.getInfinity(), "\u221E");
    applyPattern(pattern);
  }

  public void applyPattern(String pattern) {
    this.pattern = pattern;
    PatternInfo patternInfo = new PatternInfo(pattern);
    multiplier = patternInfo.multiplier;
    //curPosPrefix = patternInfo.posPrefix;
    //curPosSuffix = patternInfo.posSuffix;
    //useCurrencyCode = false;
    //int idx = curPosPrefix.indexOf(CURRENCY_SYMBOL);
    //monetaryPrefix = (idx >= 0);
    //if (monetaryPrefix) {
    //  useCurrencyCode |= (idx < curPosPrefix.length()
    //      && curPosPrefix.charAt(idx + 1) == CURRENCY_SYMBOL);
    //}
    //idx = curPosSuffix.indexOf(CURRENCY_SYMBOL);
    //monetarySuffix = (idx >= 0);
    //if (monetarySuffix) {
    //  useCurrencyCode |= (idx < curPosSuffix.length()
    //      && curPosSuffix.charAt(idx + 1) == CURRENCY_SYMBOL);
    //}
    //monetary = monetaryPrefix | monetarySuffix;
    //curNegPrefix = patternInfo.negPrefix;
    //curNegSuffix = patternInfo.negSuffix;
    //if ((curNegPrefix == null || curNegPrefix.length() == 0)
    //    && (curNegSuffix == null || curNegSuffix.length() == 0)) {
    //  curNegPrefix = String.valueOf(formatSymbols.getMinusSign()) + curPosPrefix;
    //  curNegSuffix = "";
    //}
    posPrefix = "";
    posSuffix = "";
    negPrefix = "";
    negSuffix = "";
    groupingSize = patternInfo.groupingSize;
    groupingUsed = patternInfo.groupingUsed;
    useExponential = patternInfo.useExponent;
    minExponentDigits = patternInfo.minExpDigits;
    forceDecimalSeparator = patternInfo.forceDecimal;
    maxFractionDigits = patternInfo.maxFracDigits;
    minFractionDigits = patternInfo.minFracDigits;
    minIntegerDigits = patternInfo.minIntDigits;
    maxIntegerDigits = patternInfo.maxIntDigits;
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof DecimalFormat)) {
      return false;
    }
    DecimalFormat df = (DecimalFormat) other;
    return super.equals(df)
        && Util.nullEquals(formatSymbols, df.formatSymbols)
        && Util.nullEquals(posPrefix, df.posPrefix)
        && Util.nullEquals(negPrefix, df.negPrefix)
        && Util.nullEquals(negSuffix, df.negSuffix)
        && Util.nullEquals(posSuffix, df.posSuffix)
        && multiplier == df.multiplier
        && groupingSize == df.groupingSize
        && useExponential == df.useExponential
        && minExponentDigits == df.minExponentDigits
        //&& useCurrencyCode == df.useCurrencyCode
        //&& monetary == df.monetary
        && forceDecimalSeparator == df.forceDecimalSeparator;
  }

  public DecimalFormatSymbols getDecimalFormatSymbols() {
    return formatSymbols;
  }
 
  public int getGroupingSize() {
    return groupingSize;
  }

  public int getMultiplier() {
    return multiplier;
  }
 
  public String getNegativePrefix() {
    return negPrefix;
  }
 
  public String getNegativeSuffix() {
    return negSuffix;
  }
 
  public String getPositivePrefix() {
    return posPrefix;
  }
 
  public String getPositiveSuffix() {
    return posSuffix;
  }
 
  @Override
  public int hashCode() {
    return super.hashCode() * 2
        + Util.nullHash(formatSymbols) * 3
        + Util.nullHash(posPrefix) * 5
        + Util.nullHash(negPrefix) * 7
        + Util.nullHash(negSuffix) * 11
        + Util.nullHash(posSuffix) * 13
        + multiplier * 17
        + groupingSize * 19
        + (useExponential ? 23 : 0)
        + minExponentDigits * 29
        //+ (useCurrencyCode ? 31 : 0)
        //+ (monetary ? 37 : 0)
        + (forceDecimalSeparator ? 41 : 0);
  }
 
  public boolean isDecimalSeparatorAlwaysShown() {
    return forceDecimalSeparator;
  }
 
  public void setDecimalFormatSymbols(DecimalFormatSymbols formatSymbols) {
    this.formatSymbols = formatSymbols;
  }

  public void setDecimalSeparatorAlwaysShown(boolean forceDecimalSeparator) {
    this.forceDecimalSeparator = forceDecimalSeparator;
  }
 
  public void setGroupingSize(int groupingSize) {
    this.groupingSize = groupingSize;
  }
 
  public void setMultiplier(int multiplier) {
    this.multiplier = multiplier;
  }
 
  public void setNegativePrefix(String negPrefix) {
    //this.curNegPrefix = negPrefix;
    this.negPrefix = "";
  }
 
  public void setNegativeSuffix(String negSuffix) {
    //this.curNegSuffix = negSuffix;
    this.negSuffix = "";
  }
 
  public void setPositivePrefix(String posPrefix) {
    //this.curPosPrefix = posPrefix;
    //int idx = posPrefix.indexOf(CURRENCY_SYMBOL);
    //monetaryPrefix = (idx >= 0);
    //if (monetaryPrefix) {
    //  useCurrencyCode = (idx < posPrefix.length() && posPrefix.charAt(idx + 1) == CURRENCY_SYMBOL);
    //}
    //monetary = monetaryPrefix | monetarySuffix;
    this.posPrefix = posPrefix;//currencySubstitute(posPrefix);
  }
 
  public void setPositiveSuffix(String posSuffix) {
    //this.curPosSuffix = posSuffix;
    //int idx = posSuffix.indexOf(CURRENCY_SYMBOL);
    //monetarySuffix = idx >= 0;
    //if (monetarySuffix) {
    //  useCurrencyCode = (idx < posSuffix.length() && posSuffix.charAt(idx + 1) == CURRENCY_SYMBOL);
    //}
    //monetary = monetaryPrefix | monetarySuffix;
    this.posSuffix = posSuffix;//currencySubstitute(posSuffix);
  }
 
  // TODO(jat): BigDecimal support
  //    isParseBigDecimal
  //    setParseBigDecimal
 
  public String toLocalizedPattern() {
    String locPattern = toPattern();
    locPattern = locPattern.replace('.', formatSymbols.getDecimalSeparator());
    locPattern = locPattern.replace(',', formatSymbols.getGroupingSeparator());
    locPattern = locPattern.replace('#', formatSymbols.getDigit());
    locPattern = locPattern.replace("E", formatSymbols.getExponentSeparator());
    locPattern = locPattern.replace('%', formatSymbols.getPercent());
    locPattern = locPattern.replace('\u2030', formatSymbols.getPerMill());
    locPattern = locPattern.replace('0', formatSymbols.getZeroDigit());
    locPattern = locPattern.replace('-', formatSymbols.getMinusSign());
    locPattern = locPattern.replace("\u221E", formatSymbols.getInfinity());
    return locPattern;
  }
 
  public String toPattern() {
    if (pattern != null) {
      return pattern;
    }
    // TODO(jat): implement
    return null;
  }
 
  @Override
  protected StringBuffer format(double number, StringBuffer buf) {
    if (Double.isNaN(number)) {
      buf.append(formatSymbols.getNaN());
      return buf;
    }
    boolean isNegative = ((number < 0.0) || (number == 0.0 && 1 / number < 0.0));
    buf.append(isNegative ? negPrefix : posPrefix);
    if (Double.isInfinite(number)) {
      buf.append(formatSymbols.getInfinity());
    } else {
      if (isNegative) {
        number = -number;
      }
      number *= multiplier;
      if (useExponential) {
        subformatExponential(number, buf);
      } else {
        subformatFixed(number, buf, minIntegerDigits);
      }
    }
    buf.append(isNegative ? negSuffix : posSuffix);
    return buf;
  }
 
  @Override
  protected StringBuffer format(long number, StringBuffer buf) {
    boolean isNegative = number < 0;
    buf.append(isNegative ? negPrefix : posPrefix);
    if (isNegative) {
      number = -number;
    }
    number *= multiplier;
    if (useExponential) {
      subformatExponential(number, buf);
    } else {
      subformatFixed(number, buf, minIntegerDigits);
    }

    buf.append(isNegative ? negSuffix : posSuffix);
    return buf;
  }
 
  /**
   * This method formats the exponent part of a double.
   *
   * @param exponent exponential value
   * @param result formatted exponential part will be append to it
   */
  private void addExponentPart(int exponent, StringBuffer result) {
    result.append(formatSymbols.getExponentSeparator());

    if (exponent < 0) {
      exponent = -exponent;
      result.append(formatSymbols.getMinusSign());
    }

    String exponentDigits = String.valueOf(exponent);
    for (int i = exponentDigits.length(); i < minExponentDigits; ++i) {
      result.append(formatSymbols.getZeroDigit());
    }
    result.append(exponentDigits);
  }
 
  @SuppressWarnings("unused")
private int getDigit(char ch) {
    char zero = formatSymbols.getZeroDigit();
    if (ch >= zero && ch <= zero + 9) {
      return ch - zero;
    }
    return Character.digit(ch, 10);
  }

  /**
   * This does the work of String.valueOf(long), but given a double as input
   * and avoiding our emulated longs.  Contrasted with String.valueOf(double),
   * it ensures (a) there will be no trailing .0, and (b) unwinds E-notation.
   *  
   * @param number the integral value to convert
   * @return the string representing that integer
   */
  private String makeIntString(double number) {
    return String.valueOf(number);
  }

  /**
   * This method formats a <code>double</code> in exponential format.
   *
   * @param number value need to be formated
   * @param result where the formatted string goes
   */
  private void subformatExponential(double number, StringBuffer result) {
    if (number == 0.0) {
      subformatFixed(number, result, minIntegerDigits);
      addExponentPart(0, result);
      return;
    }

    int exponent = (int) Math.floor(Math.log(number) / Math.log(10));
    number /= Math.pow(10, exponent);

    int minIntDigits = minIntegerDigits;
    if (maxIntegerDigits > 1 && maxIntegerDigits > minIntegerDigits) {
      // A repeating range is defined; adjust to it as follows.
      // If repeat == 3, we have 6,5,4=>3; 3,2,1=>0; 0,-1,-2=>-3;
      // -3,-4,-5=>-6, etc. This takes into account that the
      // exponent we have here is off by one from what we expect;
      // it is for the format 0.MMMMMx10^n.
      while ((exponent % maxIntegerDigits) != 0) {
        number *= 10;
        exponent--;
      }
      minIntDigits = 1;
    } else {
      // No repeating range is defined; use minimum integer digits.
      if (minIntegerDigits < 1) {
        exponent++;
        number /= 10;
      } else {
        for (int i = 1; i < minIntegerDigits; i++) {
          exponent--;
          number *= 10;
        }
      }
    }

    subformatFixed(number, result, minIntDigits);
    addExponentPart(exponent, result);
  }

  /**
   * This method formats a <code>long</code> in exponential format.
   *
   * @param number value need to be formated
   * @param result where the formatted string goes
   */
  private void subformatExponential(long number, StringBuffer result) {
    if (number == 0) {
      subformatFixed(number, result, minIntegerDigits);
      addExponentPart(0, result);
      return;
    }

    // TODO(jat): roundoff issues converting long to double?
    int exponent = (int) Math.floor(Math.log(number) / Math.log(10));
    number /= Math.pow(10, exponent);

    int minIntDigits = minIntegerDigits;
    if (maxIntegerDigits > 1 && maxIntegerDigits > minIntegerDigits) {
      // A repeating range is defined; adjust to it as follows.
      // If repeat == 3, we have 6,5,4=>3; 3,2,1=>0; 0,-1,-2=>-3;
      // -3,-4,-5=>-6, etc. This takes into account that the
      // exponent we have here is off by one from what we expect;
      // it is for the format 0.MMMMMx10^n.
      while ((exponent % maxIntegerDigits) != 0) {
        number *= 10;
        exponent--;
      }
      minIntDigits = 1;
    } else {
      // No repeating range is defined; use minimum integer digits.
      if (minIntegerDigits < 1) {
        exponent++;
        number /= 10;
      } else {
        for (int i = 1; i < minIntegerDigits; i++) {
          exponent--;
          number *= 10;
        }
      }
    }

    subformatFixed(number, result, minIntDigits);
    addExponentPart(exponent, result);
  }

  /**
   * This method formats a <code>double</code> into a fractional
   * representation.
   *
   * @param number value need to be formated
   * @param result result will be written here
   * @param minIntDigits minimum integer digits
   */
  private void subformatFixed(double number, StringBuffer result,
      int minIntDigits) {
    // Round the number.
    double power = Math.pow(10, maxFractionDigits);
    double intValue = Math.floor(number);
    // we don't want to use Math.round, 'cause that returns a long which JS
    // then has to emulate... Math.floor(x + 0.5d) is defined to be equivalent
    double fracValue = Math.floor((number - intValue) * power + 0.5d);
    if (fracValue >= power) {
      intValue += 1.0;
      fracValue -= power;
    }

    boolean fractionPresent = (minFractionDigits > 0) || (fracValue > 0);

    String intPart = makeIntString(intValue);
    // TODO(jat): do we need a monetary grouping separator?
    char grouping = formatSymbols.getGroupingSeparator();
    char decimal = formatSymbols.getDecimalSeparator(); //monetary ? formatSymbols.getMonetaryDecimalSeparator()
    //    : formatSymbols.getDecimalSeparator();

    int zeroDelta = formatSymbols.getZeroDigit() - '0';
    int digitLen = intPart.length();
   
    if (intValue > 0 || minIntDigits > 0) {
      for (int i = digitLen; i < minIntDigits; i++) {
        result.append(formatSymbols.getZeroDigit());
      }

      for (int i = 0; i < digitLen; i++) {
        result.append((char) (intPart.charAt(i) + zeroDelta));

        if (digitLen - i > 1 && groupingSize > 0
            && ((digitLen - i) % groupingSize == 1)) {
          result.append(grouping);
        }
      }
    } else if (!fractionPresent) {
      // If there is no fraction present, and we haven't printed any
      // integer digits, then print a zero.
      result.append(formatSymbols.getZeroDigit());
    }

    // Output the decimal separator if necessary.
    if (forceDecimalSeparator || fractionPresent) {
      result.append(decimal);
    }

    // To make sure a leading zero will be kept.
    String fracPart = makeIntString(Math.floor(fracValue + power + 0.5d));
    int fracLen = fracPart.length();
    while (fracPart.charAt(fracLen - 1) == '0' && fracLen > minFractionDigits + 1) {
      fracLen--;
    }

    for (int i = 1; i < fracLen; i++) {
      result.append((char) (fracPart.charAt(i) + zeroDelta));
    }
  }

  /**
   * This method formats a non-negative <code>long</code> into a fractional
   * representation.
   *
   * @param number value need to be formated
   * @param result result will be written here
   * @param minIntDigits minimum integer digits
   */
  private void subformatFixed(long number, StringBuffer result,
      int minIntDigits) {
    boolean fractionPresent = minFractionDigits > 0;

    String intPart = String.valueOf(number);
    // TODO(jat): do we need a monetary grouping separator?
    char grouping = formatSymbols.getGroupingSeparator();
    char decimal = formatSymbols.getDecimalSeparator(); //monetary ? formatSymbols.getMonetaryDecimalSeparator()
    //    : formatSymbols.getDecimalSeparator();

    int zeroDelta = formatSymbols.getZeroDigit() - '0';
    int digitLen = intPart.length();
   
    if (number > 0 || minIntDigits > 0) {
      for (int i = digitLen; i < minIntDigits; i++) {
        result.append(formatSymbols.getZeroDigit());
      }

      for (int i = 0; i < digitLen; i++) {
        result.append((char) (intPart.charAt(i) + zeroDelta));

        if (digitLen - i > 1 && groupingSize > 0
            && ((digitLen - i) % groupingSize == 1)) {
          result.append(grouping);
        }
      }
    } else if (!fractionPresent) {
      // If there is no fraction present, and we haven't printed any
      // integer digits, then print a zero.
      result.append(formatSymbols.getZeroDigit());
    }

    // Output the decimal separator if we always do so.
    if (forceDecimalSeparator || fractionPresent) {
      result.append(decimal);
    }
    for (int i = 0; i < minFractionDigits; i++) {
      result.append(formatSymbols.getZeroDigit());
    }
  }
}
