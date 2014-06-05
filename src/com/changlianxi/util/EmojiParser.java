package com.changlianxi.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmojiParser {
	private static final Map<String, String> IEmojiMap;
	static {
		IEmojiMap = new HashMap<String, String>();
		IEmojiMap.put(":smile:", "[smile]");
		IEmojiMap.put(":blush:", "[blush]");
		IEmojiMap.put(":smiley:", "[smiley]");
		IEmojiMap.put(":relaxed:", "[relaxed]");
		IEmojiMap.put(":smirk:", "[smirk]");
		IEmojiMap.put(":heart_eyes:", "[heart_eyes]");
		IEmojiMap.put(":kissing_heart:", "[kissing_heart]");
		IEmojiMap.put(":kissing_closed_eyes:", "[kissing_closed_eyes]");
		IEmojiMap.put(":flushed:", "[flushed]");
		IEmojiMap.put(":relieved:", "[relieved]");
		IEmojiMap.put(":grin:", "[grin]");
		IEmojiMap.put(":stuck_out_tongue_winking_eye:",
				"[stuck_out_tongue_winking_eye]");
		IEmojiMap.put(":stuck_out_tongue_closed_eyes:",
				"[stuck_out_tongue_closed_eyes]");
		IEmojiMap.put(":worried:", "[worried]");
		IEmojiMap.put(":confused:", "[confused]");
		IEmojiMap.put(":wink:", "[wink]");
		IEmojiMap.put(":sweat:", "[sweat]");
		IEmojiMap.put(":pensive:", "[pensive]");
		IEmojiMap.put(":disappointed:", "[disappointed]");
		IEmojiMap.put(":confounded:", "[confounded]");
		IEmojiMap.put(":disappointed_relieved:", "[disappointed_relieved]");
		IEmojiMap.put(":fearful:", "[fearful]");
		IEmojiMap.put(":cold_sweat:", "[cold_sweat]");
		IEmojiMap.put(":cry:", "[cry]");
		IEmojiMap.put(":sob:", "[sob]");
		IEmojiMap.put(":joy:", "[joy]");
		IEmojiMap.put(":astonished:", "[astonished]");
		IEmojiMap.put(":scream:", "[scream]");
		IEmojiMap.put(":angry:", "[angry]");
		IEmojiMap.put(":rage:", "[rage]");
		IEmojiMap.put(":sleepy:", "[sleepy]");
		IEmojiMap.put(":mask:", "[mask]");
		IEmojiMap.put(":no_mouth:", "[no_mouth]");
		IEmojiMap.put(":alien:", "[alien]");
		IEmojiMap.put(":smiling_imp:", "[smiling_imp]");
		IEmojiMap.put(":innocent:", "[innocent]");
		IEmojiMap.put(":heart:", "[heart]");
		IEmojiMap.put(":broken_heart:", "[broken_heart]");
		IEmojiMap.put(":cupid:", "[cupid]");
		IEmojiMap.put(":two_hearts:", "[two_hearts]");
		IEmojiMap.put(":sparkling_heart:", "[sparkling_heart]");
		IEmojiMap.put(":sparkles:", "[sparkles]");
		IEmojiMap.put(":star:", "[star]");
		IEmojiMap.put(":zzz:", "[zzz]");
		IEmojiMap.put(":dash:", "[dash]");
		IEmojiMap.put(":sweat_drops:", "[sweat_drops]");
		IEmojiMap.put(":musical_note:", "[musical_note]");
		IEmojiMap.put(":fire:", "[fire]");
		IEmojiMap.put(":hankey:", "[hankey]");
		IEmojiMap.put(":shit:", "[shit]");
		IEmojiMap.put(":thumbsup:", "[thumbsup]");
		IEmojiMap.put(":thumbsdown:", "[thumbsdown]");
		IEmojiMap.put(":ok_hand:", "[ok_hand]");
		IEmojiMap.put(":punch:", "[punch]");
		IEmojiMap.put(":facepunch:", "[facepunch]");
		IEmojiMap.put(":fist:", "[fist]");
		IEmojiMap.put(":wave:", "[wave]");
		IEmojiMap.put(":hand:", "[hand]");
		IEmojiMap.put(":point_up:", "[point_up]");
		IEmojiMap.put(":pray:", "[pray]");
		IEmojiMap.put(":clap:", "[clap]");
		IEmojiMap.put(":muscle:", "[muscle]");
		IEmojiMap.put(":metal:", "[metal]");
		IEmojiMap.put(":fu:", "[fu]");
		IEmojiMap.put(":walking:", "[walking]");
		IEmojiMap.put(":runner:", "[runner]");
		IEmojiMap.put(":couple:", "[couple]");
		IEmojiMap.put(":family:", "[family]");
		IEmojiMap.put(":bow:", "[bow]");
		IEmojiMap.put(":couplekiss:", "[couplekiss]");
		IEmojiMap.put(":couple_with_heart:", "[couple_with_heart]");
		IEmojiMap.put(":massage:", "[massage]");
		IEmojiMap.put(":boy:", "[boy]");
		IEmojiMap.put(":girl:", "[girl]");
		IEmojiMap.put(":woman:", "[woman]");
		IEmojiMap.put(":man:", "[man]");
		IEmojiMap.put(":baby:", "[baby]");
		IEmojiMap.put(":older_woman:", "[older_woman]");
		IEmojiMap.put(":older_man:", "[older_man]");
		IEmojiMap.put(":person_with_blond_hair:", "[person_with_blond_hair]");
		IEmojiMap.put(":man_with_gua_pi_mao:", "[man_with_gua_pi_mao]");
		IEmojiMap.put(":man_with_turban:", "[man_with_turban]");
		IEmojiMap.put(":construction_worker:", "[construction_worker]");
		IEmojiMap.put(":cop:", "[cop]");
		IEmojiMap.put(":angel:", "[angel]");
		IEmojiMap.put(":princess:", "[princess]");
		IEmojiMap.put(":smiley_cat:", "[smiley_cat]");
		IEmojiMap.put(":kiss:", "[kiss]");

	}

	/**
	 * Return the text with emoticons changed to android code
	 * 
	 * @param text
	 * @return
	 */
	public static String demojizedText(String text) {
		String returnTextString = text;
		// Pattern to match
		Pattern pattern = Pattern.compile("(\\:[^\\:]+\\:)");
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			String found = matcher.group();
			if (IEmojiMap.get(found) == null)
				continue;
			returnTextString = returnTextString.replace(found,
					IEmojiMap.get(found));
		}
		// Returning text
		return returnTextString;
	}
}
