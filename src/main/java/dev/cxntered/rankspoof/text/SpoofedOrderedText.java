package dev.cxntered.rankspoof.text;

import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class SpoofedOrderedText implements OrderedText {
    private final List<StyledCharacter> styledCharacters = new ArrayList<>(64);

    public SpoofedOrderedText(OrderedText original) {
        List<StyledCharacter> originalCharacters = new ArrayList<>(64);

        original.accept((index, style, codePoint) -> {
            originalCharacters.add(new StyledCharacter(codePoint, style));
            return true;
        });

//        StringBuilder stringBuilder = new StringBuilder();
        for (StyledCharacter sc : originalCharacters) {
//            stringBuilder.append(Character.toChars(sc.codePoint()));
//            if (sc.codePoint() == 'a') {
//                String text = "HELP";
//                for (int j = 0; j < text.length(); j++) {
//                    char c = text.charAt(j);
//                    styledCharacters.add(new StyledCharacter(c, Style.EMPTY.withColor(Formatting.DARK_RED).withBold(true)));
//                }
//            } else {
                styledCharacters.add(new StyledCharacter(sc.codePoint(), sc.style()));
//            }
        }
//        String string = stringBuilder.toString();
//        System.out.println("Original string: " + string);
    }

    @Override
    public boolean accept(CharacterVisitor visitor) {
        for (int i = 0; i < styledCharacters.size(); i++) {
            StyledCharacter sc = styledCharacters.get(i);
            if (!visitor.accept(i, sc.style, sc.codePoint)) return false;
        }
        return true;
    }

    private record StyledCharacter(int codePoint, Style style) {
    }
}
