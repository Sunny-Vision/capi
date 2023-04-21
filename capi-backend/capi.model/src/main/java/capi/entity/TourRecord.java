package capi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name="TourRecord")
public class TourRecord extends EntityBase{

	@GenericGenerator(name = "generator", strategy = "foreign", parameters = @Parameter(name = "property", value = "quotationRecord"))
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "TourRecordId", unique = true, nullable = false)
	private Integer tourRecordId;
	
	@OneToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn
	private QuotationRecord quotationRecord;
	
	@Column(name="Day1Price")
	private Double day1Price;
	
	@Column(name="Day2Price")
	private Double day2Price;
	
	@Column(name="Day3Price")
	private Double day3Price;
	
	@Column(name="Day4Price")
	private Double day4Price;
	
	@Column(name="Day5Price")
	private Double day5Price;
	
	@Column(name="Day6Price")
	private Double day6Price;
	
	@Column(name="Day7Price")
	private Double day7Price;
	
	@Column(name="Day8Price")
	private Double day8Price;
	
	@Column(name="Day9Price")
	private Double day9Price;
	
	@Column(name="Day10Price")
	private Double day10Price;
	
	@Column(name="Day11Price")
	private Double day11Price;
	
	@Column(name="Day12Price")
	private Double day12Price;
	
	@Column(name="Day13Price")
	private Double day13Price;
	
	@Column(name="Day14Price")
	private Double day14Price;
	
	@Column(name="Day15Price")
	private Double day15Price;
	
	@Column(name="Day16Price")
	private Double day16Price;
	
	@Column(name="Day17Price")
	private Double day17Price;
	
	@Column(name="Day18Price")
	private Double day18Price;
	
	@Column(name="Day19Price")
	private Double day19Price;	
	
	@Column(name="Day20Price")
	private Double day20Price;
	
	@Column(name="Day21Price")
	private Double day21Price;
	
	@Column(name="Day22Price")
	private Double day22Price;
	
	@Column(name="Day23Price")
	private Double day23Price;
	
	@Column(name="Day24Price")
	private Double day24Price;
	
	@Column(name="Day25Price")
	private Double day25Price;
	
	@Column(name="Day26Price")
	private Double day26Price;
	
	@Column(name="Day27Price")
	private Double day27Price;
	
	@Column(name="Day28Price")
	private Double day28Price;
	
	@Column(name="Day29Price")
	private Double day29Price;
	
	@Column(name="Day30Price")
	private Double day30Price;
	
	@Column(name="Day31Price")
	private Double day31Price;
	
	@Column(name="ExtraPrice1Name")
	private String extraPrice1Name;
	
	@Column(name="ExtraPrice1Value")
	private Double extraPrice1Value;
	
	@Column(name="IsExtraPrice1Count")
	private boolean isExtraPrice1Count;
	
	@Column(name="ExtraPrice2Name")
	private String extraPrice2Name;
	
	@Column(name="ExtraPrice2Value")
	private Double extraPrice2Value;
	
	@Column(name="IsExtraPrice2Count")
	private boolean isExtraPrice2Count;	
	
	@Column(name="ExtraPrice3Name")
	private String extraPrice3Name;
	
	@Column(name="ExtraPrice3Value")
	private Double extraPrice3Value;
	
	@Column(name="IsExtraPrice3Count")
	private boolean isExtraPrice3Count;
	
	@Column(name="ExtraPrice4Name")
	private String extraPrice4Name;
	
	@Column(name="ExtraPrice4Value")
	private Double extraPrice4Value;
	
	@Column(name="IsExtraPrice4Count")
	private boolean isExtraPrice4Count;
	
	@Column(name="ExtraPrice5Name")
	private String extraPrice5Name;
	
	@Column(name="ExtraPrice5Value")
	private Double extraPrice5Value;
	
	@Column(name="IsExtraPrice5Count")
	private boolean isExtraPrice5Count;
	
	
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getTourRecordId();
	}



	public Integer getTourRecordId() {
		return tourRecordId;
	}



	public void setTourRecordId(Integer tourRecordId) {
		this.tourRecordId = tourRecordId;
	}



	public Double getDay1Price() {
		return day1Price;
	}



	public void setDay1Price(Double day1Price) {
		this.day1Price = day1Price;
	}



	public Double getDay2Price() {
		return day2Price;
	}



	public void setDay2Price(Double day2Price) {
		this.day2Price = day2Price;
	}



	public Double getDay3Price() {
		return day3Price;
	}



	public void setDay3Price(Double day3Price) {
		this.day3Price = day3Price;
	}



	public Double getDay4Price() {
		return day4Price;
	}



	public void setDay4Price(Double day4Price) {
		this.day4Price = day4Price;
	}



	public Double getDay5Price() {
		return day5Price;
	}



	public void setDay5Price(Double day5Price) {
		this.day5Price = day5Price;
	}



	public Double getDay6Price() {
		return day6Price;
	}



	public void setDay6Price(Double day6Price) {
		this.day6Price = day6Price;
	}



	public Double getDay7Price() {
		return day7Price;
	}



	public void setDay7Price(Double day7Price) {
		this.day7Price = day7Price;
	}



	public Double getDay8Price() {
		return day8Price;
	}



	public void setDay8Price(Double day8Price) {
		this.day8Price = day8Price;
	}



	public Double getDay9Price() {
		return day9Price;
	}



	public void setDay9Price(Double day9Price) {
		this.day9Price = day9Price;
	}



	public Double getDay10Price() {
		return day10Price;
	}



	public void setDay10Price(Double day10Price) {
		this.day10Price = day10Price;
	}



	public Double getDay11Price() {
		return day11Price;
	}



	public void setDay11Price(Double day11Price) {
		this.day11Price = day11Price;
	}



	public Double getDay12Price() {
		return day12Price;
	}



	public void setDay12Price(Double day12Price) {
		this.day12Price = day12Price;
	}



	public Double getDay13Price() {
		return day13Price;
	}



	public void setDay13Price(Double day13Price) {
		this.day13Price = day13Price;
	}



	public Double getDay14Price() {
		return day14Price;
	}



	public void setDay14Price(Double day14Price) {
		this.day14Price = day14Price;
	}



	public Double getDay15Price() {
		return day15Price;
	}



	public void setDay15Price(Double day15Price) {
		this.day15Price = day15Price;
	}



	public Double getDay16Price() {
		return day16Price;
	}



	public void setDay16Price(Double day16Price) {
		this.day16Price = day16Price;
	}



	public Double getDay17Price() {
		return day17Price;
	}



	public void setDay17Price(Double day17Price) {
		this.day17Price = day17Price;
	}



	public Double getDay18Price() {
		return day18Price;
	}



	public void setDay18Price(Double day18Price) {
		this.day18Price = day18Price;
	}



	public Double getDay19Price() {
		return day19Price;
	}



	public void setDay19Price(Double day19Price) {
		this.day19Price = day19Price;
	}



	public Double getDay20Price() {
		return day20Price;
	}



	public void setDay20Price(Double day20Price) {
		this.day20Price = day20Price;
	}



	public Double getDay21Price() {
		return day21Price;
	}



	public void setDay21Price(Double day21Price) {
		this.day21Price = day21Price;
	}



	public Double getDay22Price() {
		return day22Price;
	}



	public void setDay22Price(Double day22Price) {
		this.day22Price = day22Price;
	}



	public Double getDay23Price() {
		return day23Price;
	}



	public void setDay23Price(Double day23Price) {
		this.day23Price = day23Price;
	}



	public Double getDay24Price() {
		return day24Price;
	}



	public void setDay24Price(Double day24Price) {
		this.day24Price = day24Price;
	}



	public Double getDay25Price() {
		return day25Price;
	}



	public void setDay25Price(Double day25Price) {
		this.day25Price = day25Price;
	}



	public Double getDay26Price() {
		return day26Price;
	}



	public void setDay26Price(Double day26Price) {
		this.day26Price = day26Price;
	}



	public Double getDay27Price() {
		return day27Price;
	}



	public void setDay27Price(Double day27Price) {
		this.day27Price = day27Price;
	}



	public Double getDay28Price() {
		return day28Price;
	}



	public void setDay28Price(Double day28Price) {
		this.day28Price = day28Price;
	}



	public Double getDay29Price() {
		return day29Price;
	}



	public void setDay29Price(Double day29Price) {
		this.day29Price = day29Price;
	}



	public Double getDay30Price() {
		return day30Price;
	}



	public void setDay30Price(Double day30Price) {
		this.day30Price = day30Price;
	}



	public Double getDay31Price() {
		return day31Price;
	}



	public void setDay31Price(Double day31Price) {
		this.day31Price = day31Price;
	}



	public String getExtraPrice1Name() {
		return extraPrice1Name;
	}



	public void setExtraPrice1Name(String extraPrice1Name) {
		this.extraPrice1Name = extraPrice1Name;
	}



	public Double getExtraPrice1Value() {
		return extraPrice1Value;
	}



	public void setExtraPrice1Value(Double extraPrice1Value) {
		this.extraPrice1Value = extraPrice1Value;
	}



	public boolean isExtraPrice1Count() {
		return isExtraPrice1Count;
	}



	public void setExtraPrice1Count(boolean isExtraPrice1Count) {
		this.isExtraPrice1Count = isExtraPrice1Count;
	}



	public String getExtraPrice2Name() {
		return extraPrice2Name;
	}



	public void setExtraPrice2Name(String extraPrice2Name) {
		this.extraPrice2Name = extraPrice2Name;
	}



	public Double getExtraPrice2Value() {
		return extraPrice2Value;
	}



	public void setExtraPrice2Value(Double extraPrice2Value) {
		this.extraPrice2Value = extraPrice2Value;
	}



	public boolean isExtraPrice2Count() {
		return isExtraPrice2Count;
	}



	public void setExtraPrice2Count(boolean isExtraPrice2Count) {
		this.isExtraPrice2Count = isExtraPrice2Count;
	}



	public String getExtraPrice3Name() {
		return extraPrice3Name;
	}



	public void setExtraPrice3Name(String extraPrice3Name) {
		this.extraPrice3Name = extraPrice3Name;
	}



	public Double getExtraPrice3Value() {
		return extraPrice3Value;
	}



	public void setExtraPrice3Value(Double extraPrice3Value) {
		this.extraPrice3Value = extraPrice3Value;
	}



	public boolean isExtraPrice3Count() {
		return isExtraPrice3Count;
	}



	public void setExtraPrice3Count(boolean isExtraPrice3Count) {
		this.isExtraPrice3Count = isExtraPrice3Count;
	}



	public String getExtraPrice4Name() {
		return extraPrice4Name;
	}



	public void setExtraPrice4Name(String extraPrice4Name) {
		this.extraPrice4Name = extraPrice4Name;
	}



	public Double getExtraPrice4Value() {
		return extraPrice4Value;
	}



	public void setExtraPrice4Value(Double extraPrice4Value) {
		this.extraPrice4Value = extraPrice4Value;
	}



	public boolean isExtraPrice4Count() {
		return isExtraPrice4Count;
	}



	public void setExtraPrice4Count(boolean isExtraPrice4Count) {
		this.isExtraPrice4Count = isExtraPrice4Count;
	}



	public String getExtraPrice5Name() {
		return extraPrice5Name;
	}



	public void setExtraPrice5Name(String extraPrice5Name) {
		this.extraPrice5Name = extraPrice5Name;
	}



	public Double getExtraPrice5Value() {
		return extraPrice5Value;
	}



	public void setExtraPrice5Value(Double extraPrice5Value) {
		this.extraPrice5Value = extraPrice5Value;
	}



	public boolean isExtraPrice5Count() {
		return isExtraPrice5Count;
	}



	public void setExtraPrice5Count(boolean isExtraPrice5Count) {
		this.isExtraPrice5Count = isExtraPrice5Count;
	}



	public QuotationRecord getQuotationRecord() {
		return quotationRecord;
	}



	public void setQuotationRecord(QuotationRecord quotationRecord) {
		this.quotationRecord = quotationRecord;
	}


}
