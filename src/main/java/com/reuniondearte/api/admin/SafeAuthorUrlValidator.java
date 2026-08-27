package com.reuniondearte.api.admin;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

public class SafeAuthorUrlValidator implements ConstraintValidator<SafeAuthorUrl, String> {
    private static final Pattern LOCAL_AUTHOR_AVATAR = Pattern.compile(
            "^/authors/[a-z0-9][a-z0-9-]*\\.(?:jpeg|jpg|webp|png)$"
    );
    private static final Set<Character> DISALLOWED_CHARACTERS = Set.of('\\', '<', '>', '\'', '"', '`');

    private SafeAuthorUrl.Kind kind;

    @Override
    public void initialize(SafeAuthorUrl constraintAnnotation) {
        this.kind = constraintAnnotation.kind();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        if (containsUnsafeCharacter(value)) {
            return false;
        }
        if (kind == SafeAuthorUrl.Kind.AVATAR && isLocalAuthorAvatar(value)) {
            return true;
        }
        return isSafeAbsoluteHttpUrl(value);
    }

    private boolean containsUnsafeCharacter(String value) {
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            if (Character.isISOControl(character) || Character.isWhitespace(character) || DISALLOWED_CHARACTERS.contains(character)) {
                return true;
            }
        }
        return false;
    }

    private boolean isLocalAuthorAvatar(String value) {
        return LOCAL_AUTHOR_AVATAR.matcher(value).matches();
    }

    private boolean isSafeAbsoluteHttpUrl(String value) {
        URI uri;
        try {
            uri = new URI(value);
        } catch (URISyntaxException exception) {
            return false;
        }
        String scheme = uri.getScheme();
        if (scheme == null || (!"http".equals(scheme.toLowerCase(Locale.ROOT)) && !"https".equals(scheme.toLowerCase(Locale.ROOT)))) {
            return false;
        }
        if (!uri.isAbsolute() || uri.getRawAuthority() == null || uri.getHost() == null || uri.getHost().isBlank()) {
            return false;
        }
        return uri.getRawUserInfo() == null && uri.getUserInfo() == null;
    }
}
