package org.team_rocket_unc.electronica_digital_app.units.unit_4_karnaugh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class EssentialsProcessor {

    private static Map<Integer, Set<String>> createGroup(List<Integer> mintermsIn) {
        Map<Integer,Set<String>> groups = new HashMap<>();
        for(int i = 0; i < 5; i++) {
            Set<String> binaries = new HashSet<>();
            groups.put(i,binaries);
        }
        for(Integer in:mintermsIn){
            String binary = String.format("%4s",Integer.toBinaryString(in)).replace(' ','0');
            int group = 0;
            for(int i=0;i<binary.length();i++){
                if(binary.charAt(i)=='1')
                    group++;
            }
            groups.get(group).add(binary);
        }
        return groups;
    }

    private static boolean isEmpty(Map<Integer, Set<String>> table) {
        for(Set<String> set : table.values()) {
            if(!set.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static String changesOneBit(String currentBinary, String comparedBinary) {
        List<Integer> changesIndex = new ArrayList<>();
        for(int charIndex = 0; charIndex < currentBinary.length(); charIndex++) {
            if(comparedBinary.charAt(charIndex) != currentBinary.charAt(charIndex)) {
                changesIndex.add(charIndex);
            }
        }
        if(changesIndex.size() == 1) {
            StringBuilder generalizedBinary = new StringBuilder(currentBinary);
            generalizedBinary.setCharAt(changesIndex.get(0), '-');
            return generalizedBinary.toString();
        }
        return null;
    }

    private static MergeResponse merge(Map<Integer, Set<String>> groups) {
        Map<Integer, Set<String>> implications = new HashMap<>();
        Set<String> primes = new HashSet<>();
        for(Set<String> value : groups.values()) {
            primes.addAll(value);
        }
        for(int i = 0; i < groups.size() - 1; i++) {
            Set<String> currentSet = groups.get(i);
            Set<String> nextSet = groups.get(i + 1);
            Set<String> newBinariesSet = new HashSet<>();
            for(String currentBinary : currentSet) {
                for(String comparedBinary : nextSet) {
                    String changesOneBit = changesOneBit(currentBinary, comparedBinary);
                    if(changesOneBit != null) {
                        newBinariesSet.add(changesOneBit);
                        primes.remove(comparedBinary);
                        primes.remove(currentBinary);
                    }
                }
            }
            implications.put(i, newBinariesSet);
        }
        return new MergeResponse(primes, implications);
    }

    public static Set<String> computeEssentials(List<Integer> mintermsIn) {
        Set<String> essentials = new HashSet<>();
        Map<Integer,Set<String>> nextTable = createGroup(mintermsIn);
        Map<Integer, Set<String>> table = new HashMap<>();
        while(!isEmpty(nextTable)) {
            table = new HashMap<>(nextTable);
            KarnaughPrinter.printGroups(table);
            MergeResponse mergeResponse = merge(table);
            nextTable = mergeResponse.getMerged();
            essentials.addAll(mergeResponse.getEssentials());
        }
        Map<Integer, Set<String>> finalTable = new HashMap<>(table);
        for(Set<String> value : finalTable.values()) {
            essentials.addAll(value);
        }
        return essentials;
    }

    public static List<Integer> stringToDecimal(String s) {
        List<String> strings = new ArrayList<>();
        strings.add(s);
        boolean hasConditionals = true;
        while(hasConditionals) {
            hasConditionals = false;
            List<String> newStrings = new ArrayList<>();
            for(String currentString : strings) {
                if(currentString.contains("-")) {
                    newStrings.add(currentString.replaceFirst("-", "1"));
                    newStrings.add(currentString.replaceFirst("-", "0"));
                    hasConditionals = true;
                } else {
                    newStrings.add(currentString);
                }
            }
            strings = new ArrayList<>(newStrings);
        }
        List<Integer> dec = new ArrayList<>();
        for(String string : strings) {
            dec.add(Integer.parseInt(string, 2));
        }
        return dec;
    }

    public static String string2Function(String s){
        String[] lyrics = {"A","B","C","D"};
        String[] not = {"'" ,""};
        StringBuilder out= new StringBuilder();
        for(int i=0; i<s.length(); i++){
            if(s.charAt(i)!='-'){
                out.append(lyrics[i]);
                out.append(not[Character.getNumericValue(s.charAt(i))]);
            }
        }
        return out.toString();
    }

}
