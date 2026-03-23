import { describe, expect, it } from 'vitest'
import { getBirth, formatDate, formatSeconds, parseLyric } from './index'

describe('utils functions', () => {
  describe('getBirth', () => {
    it('should return empty string for null value', () => {
      expect(getBirth(null)).toBe('')
    })

    it('should return empty string for empty string', () => {
      expect(getBirth('')).toBe('')
    })

    it('should format date correctly', () => {
      const date = new Date('2020-05-15')
      expect(getBirth(date.getTime())).toBe('2020-05-15')
    })
  })

  describe('formatDate', () => {
    it('should return empty string for null value', () => {
      expect(formatDate(null)).toBe('')
    })

    it('should return empty string for empty string', () => {
      expect(formatDate('')).toBe('')
    })

    it('should format date with time correctly', () => {
      const date = new Date('2020-05-15T14:30:45')
      expect(formatDate(date.getTime())).toBe('2020-05-15 14:30:45')
    })
  })

  describe('formatSeconds', () => {
    it('should format seconds less than a minute', () => {
      expect(formatSeconds(45)).toBe('0:45')
    })

    it('should format seconds with leading zero for minutes', () => {
      expect(formatSeconds(90)).toBe('1:30')
    })

    it('should format hours correctly', () => {
      expect(formatSeconds(3661)).toBe('1:01:01')
    })
  })

  describe('parseLyric', () => {
    it('should return [[0, text]] for text without time pattern', () => {
      expect(parseLyric('hello world')).toEqual([[0, 'hello world']])
    })

    it('should parse lyric with time pattern', () => {
      const lyric = '[00:12.34]Hello\n[00:15.67]World'
      const result = parseLyric(lyric)
      expect(result[0]).toEqual([12.34, 'Hello'])
      expect(result[1]).toEqual([15.67, 'World'])
    })
  })
})