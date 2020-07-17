package com.cinque.common.util;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class ApplicationMessageResource {

	private Locale defaultLocale = Locale.ENGLISH;

	@Autowired
	@Qualifier("messageResource")
	private MessageSource messageSource;

	@Nullable
	public String getMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage, Locale locale) {
		return messageSource.getMessage(code, args, defaultMessage, locale);
	}

	@Nullable
	public String getMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage) {
		return messageSource.getMessage(code, args, defaultMessage, defaultLocale);
	}

	public Locale getDefaultLocale() {
		return defaultLocale;
	}

	public void setDefaultLocale(Locale defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	@Nullable
	public String getMessage(String code, @Nullable Object[] args, Locale locale) {
		return messageSource.getMessage(code, args, locale);
	}

	@Nullable
	public String getMessage(String code, @Nullable Object[] args) {
		// defaultLocale =
		// LocaleContextHolder.getLocale(LocaleContextHolder.getLocaleContext());
		return messageSource.getMessage(code, args, defaultLocale);
	}
}
